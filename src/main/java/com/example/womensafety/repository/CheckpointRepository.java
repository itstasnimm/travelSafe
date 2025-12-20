package com.example.womensafety.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.womensafety.entity.Checkpoint;
import com.example.womensafety.entity.Journey;

@Repository
public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {
    List<Checkpoint> findByJourney(Journey j);
}
