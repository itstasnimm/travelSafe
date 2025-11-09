package com.example.womensafety.entity;

import jakarta.persistence.*;

@Entity
public class Checkpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name or label for the checkpoint (e.g., "Bus Stop")
    private String name;

    // Optional precise position
    private Double latitude;
    private Double longitude;

    private boolean reached = false;

    @ManyToOne
    @JoinColumn(name = "journey_id")
    private Journey journey;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public boolean isReached() { return reached; }
    public void setReached(boolean reached) { this.reached = reached; }

    public Journey getJourney() { return journey; }
    public void setJourney(Journey journey) { this.journey = journey; }

    @Override
    public String toString() {
        return name + " (" + latitude + "," + longitude + ")";
    }
}
