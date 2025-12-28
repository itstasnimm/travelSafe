package com.example.womensafety.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.womensafety.entity.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, Long>{
    Users findByUname(String uname);
    Users findByUnameUpassword(String uname,String upassword);
}