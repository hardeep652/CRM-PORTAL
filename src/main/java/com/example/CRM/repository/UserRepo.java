package com.example.CRM.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CRM.model.Users;

public interface UserRepo extends JpaRepository<Users, Long> {
    // Additional query methods can be defined here if needed

}
