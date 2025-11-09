package com.example.womensafety.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.womensafety.entity.Checkpoint;

public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {
}
