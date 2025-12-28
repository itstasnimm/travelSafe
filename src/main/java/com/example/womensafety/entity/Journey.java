package com.example.womensafety.entity;

// import com.example.womensafety.entity.Users;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name="journeys")
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Can be a place name or "lat,lng"
    private String source;
    private String destination;

    // Unique shareable track id
    @Column(unique = true, nullable = false)
    private String trackId;

    // Last known location of the user (who is being tracked)
    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;


     @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Checkpoint> checkpoints = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private Users user;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }

    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }

    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }

    public List<Checkpoint> getCheckpoints() { return checkpoints; }
    public void setCheckpoints(List<Checkpoint> checkpoints) { 
        this.checkpoints = checkpoints; 
        if (this.checkpoints != null) {
            for (Checkpoint c : this.checkpoints) c.setJourney(this);
        }
    }

    // public List<Checkpoint> getCheckpointList() {
    //     return checkpoints == null ? List.of() : checkpoints;
    // }

    public Users getUser() {
        return user;
    }
    public void setUser(Users user) {
        this.user = user;
    }

}
