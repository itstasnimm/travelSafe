package com.example.womensafety.service;

import com.example.womensafety.entity.Journey;
import com.example.womensafety.entity.SafePlace;
import com.example.womensafety.entity.Checkpoint;
import com.example.womensafety.repository.JourneyRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final RestTemplate restTemplate;
    
    // Constructor injection for repository
    public JourneyService(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
        this.restTemplate = new RestTemplate();
    }

    //Create a new journey with checkpoints
     //Checkpoints in CSV form (ex: "Home@12.9,77.5;Office@12.8,77.6")
     
    public Journey createJourney(String source, String destination, String checkpointsCsv) {
        Journey j = new Journey();
        j.setSource(source);
        j.setDestination(destination);
        j.setTrackId(UUID.randomUUID().toString()); // unique sharable id

        List<Checkpoint> cps = new ArrayList<>();

        if (checkpointsCsv != null) {
            String[] entries = checkpointsCsv.split(";"); // semicolon separates checkpoints
            int autoIdx = 1;

            for (String entry : entries) {
                String t = entry == null ? "" : entry.trim();
                if (t.isEmpty()) continue; // skip blanks

                Checkpoint cp = new Checkpoint();

                if (t.contains("@")) {
                    // Format: "Name@lat,lng"
                    String[] nameAndCoords = t.split("@", 2);
                    cp.setName(nameAndCoords[0].trim());

                    String coords = nameAndCoords.length > 1 ? nameAndCoords[1].trim() : "";
                    String[] ll = coords.split(",");
                    if (ll.length == 2) {
                        try {
                            cp.setLatitude(Double.parseDouble(ll[0].trim()));
                            cp.setLongitude(Double.parseDouble(ll[1].trim()));
                        } catch (NumberFormatException nfe) {
                            // if parsing fails, we just keep the name
                        }
                    }
                } else if (t.contains(",")) {
                    // Format: "lat,lng" (no explicit name)
                    String[] ll = t.split(",");
                    if (ll.length == 2) {
                        try {
                            cp.setLatitude(Double.parseDouble(ll[0].trim()));
                            cp.setLongitude(Double.parseDouble(ll[1].trim()));
                            cp.setName("CP " + autoIdx++); // auto-generated name
                        } catch (NumberFormatException ignored) {
                            // fallback to treating entire entry as a name
                        }
                    }
                    if (cp.getName() == null) {
                        cp.setName(t);
                    }
                } else {
                    // Only name given
                    cp.setName(t);
                }

                cp.setJourney(j); // link checkpoint back to journey
                cps.add(cp);
            }
        }

        j.setCheckpoints(cps); // set list + back references
        return journeyRepository.save(j); // persist in DB
    }

    // Get journey by trackId
    public Journey getByTrackId(String trackId) {
        return journeyRepository.findByTrackId(trackId); 
    }

    public Journey updateLocation(String trackId, Double latitude, Double longitude) {
        Journey j = journeyRepository.findByTrackId(trackId);
        if (j != null) {
            j.setCurrentLatitude(latitude);
            j.setCurrentLongitude(longitude);
            return journeyRepository.save(j);
        }
        return null;
    }

    public List<SafePlace> getSafePlaces(String trackId) {
        List<SafePlace> result = new ArrayList<>();

        // 1️⃣ Fetch coordinates from DB
        Journey journey = journeyRepository.findByTrackId(trackId);
        if (journey == null) {
            return result; // or throw custom exception
        }

        double lat = journey.getCurrentLatitude();
        double lon = journey.getCurrentLongitude();

        List<SafePlace> hospitals = fetchPlaces("hospital", lat, lon);
        List<SafePlace> policeStations = fetchPlaces("police", lat, lon);

        List<SafePlace> topHospitals = hospitals.stream().limit(2).collect(Collectors.toList());
        List<SafePlace> topPolice = policeStations.stream().limit(2).collect(Collectors.toList());

        // Combine them
        List<SafePlace> nearbySafePlaces = new ArrayList<>();
        nearbySafePlaces.addAll(topHospitals);
        nearbySafePlaces.addAll(topPolice);

        return nearbySafePlaces;
    }

//  "https://overpass-api.de/api/interpreter?data=[out:json];"+ "node[\"amenity\"=\"" + amenityType + "\"](around:5000," + lat + "," + lon + ");out;";

    private List<SafePlace> fetchPlaces(String type, double lat, double lon) {
        String url = "https://overpass.kumi.systems/api/interpreter?data=[out:json];" +
            "node[\"amenity\"=\"" + type + "\"](around:2500," + lat + "," + lon + ");out;";

        // Step 1: Get raw response as JSON map
        Map<String, Object> raw = restTemplate.getForObject(url, Map.class);
        if (raw == null || !raw.containsKey("elements")) return List.of();

        List<Map<String, Object>> elements = (List<Map<String, Object>>) raw.get("elements");

        // Step 2: Convert each element to SafePlace (with name + distance)
        return elements.stream()
            .map(el -> {
                Map<String, Object> tags = (Map<String, Object>) el.get("tags");
                String name = tags != null ? (String) tags.get("name") : "Unknown";
                double plat = (double) el.get("lat");
                double plng = (double) el.get("lon");
                double distance = calculateDistance(lat, lon, plat, plng);

                SafePlace place = new SafePlace();
                place.setPname(name);
                place.setPlat(plat);
                place.setPlng(plng);
                place.setPdistance(distance);
                return place;
            })
            .sorted(Comparator.comparingDouble(SafePlace::getPdistance))
            .collect(Collectors.toList());
    }



    // 🔹 Haversine formula for distance in km
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0; // 2 decimal km
    }
}
