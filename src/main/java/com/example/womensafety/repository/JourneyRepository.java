package com.example.womensafety.repository;

import com.example.womensafety.entity.Journey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long>{
    Journey findByTrackId(String trackId);    
}