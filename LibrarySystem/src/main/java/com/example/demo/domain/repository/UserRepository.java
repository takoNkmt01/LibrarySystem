package com.example.demo.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.model.UserJPA;

public interface UserRepository extends JpaRepository<UserJPA, String>{
}
