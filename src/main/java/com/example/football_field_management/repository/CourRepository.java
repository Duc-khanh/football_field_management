package com.example.football_field_management.repository;

import com.example.football_field_management.model.Cour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourRepository extends JpaRepository<Cour, Long> {
}
