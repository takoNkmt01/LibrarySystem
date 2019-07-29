package com.example.demo.login.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.login.domain.model.UserJPA;

public interface UserRepository extends JpaRepository<UserJPA, String>{
}
