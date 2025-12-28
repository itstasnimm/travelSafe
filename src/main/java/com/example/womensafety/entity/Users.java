package com.example.womensafety.entity;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.Data;

@Entity
@Table(name="users")
@Data
public class Users{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    
    private String uname;
    private String upassword;

    @OneToMany(mappedBy = "user", cascade=CascadeType.ALL)
    private List<Journey> journeys = new ArrayList<>();
}
