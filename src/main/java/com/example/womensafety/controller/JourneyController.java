package com.example.womensafety.controller;

import com.example.womensafety.service.JourneyService;
import com.example.womensafety.entity.SafePlace;
import com.example.womensafety.entity.Journey;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/journey")
public class JourneyController{

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    // Page 1: show form
    @GetMapping("/form")
    public String showCreateForm() {
        return "createJourney";
    }

    // Handle form submit → after saving go to liveTracking.html
    @PostMapping("/create")
    public String createJourney(@RequestParam String source,
                                @RequestParam String destination,
                                @RequestParam(required = false) String checkpoints,
                                Model model) {
        Journey saved = journeyService.createJourney(source, destination, checkpoints);
        model.addAttribute("journey", saved);
        model.addAttribute("trackLink", "/journey/track/" + saved.getTrackId());
        return "liveTracking";  // Traveller’s live tracking page
    }

    // Page 2: trusted contact opens this link
    @GetMapping("/track/{trackId}")
    public String trackJourney(@PathVariable String trackId,
                               Model model) {
        Journey j = journeyService.getByTrackId(trackId);
        model.addAttribute("journey", j);
        model.addAttribute("trackId", trackId);
        return "trackJourney";  // Trusted contact’s tracking page
    }

    // Traveller’s device updates current location every 30s
    @PostMapping("/track/{trackId}/update-location")
    @ResponseBody
    public String updateLocation(@PathVariable String trackId,
                                 @RequestParam("latitude") Double latitude,
                                 @RequestParam("longitude") Double longitude) {
        Journey j = journeyService.updateLocation(trackId, latitude, longitude);
        return (j == null) ? "not-found" : "ok";
    }

    // Trusted contacts poll this API to fetch current location
    @GetMapping("/track/{trackId}/location")
    @ResponseBody
    public Map<String, Double> getCurrentLocation(@PathVariable String trackId) {
        Journey j = journeyService.getByTrackId(trackId);
        if (j == null) return Collections.emptyMap();
//setting var names same as tracking.js
        Map<String, Double> response = new HashMap<>();
        response.put("lat", j.getCurrentLatitude());
        response.put("lng", j.getCurrentLongitude());
        return response;
    }

    @GetMapping("/track/{trackId}/nearest-safehouse")
    @ResponseBody
    public List<SafePlace> nearestSafe(@PathVariable String trackId){
        try {
            return journeyService.getSafePlaces(trackId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
}