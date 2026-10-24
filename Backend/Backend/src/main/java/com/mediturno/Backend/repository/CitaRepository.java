package com.mediturno.Backend.repository;

import com.mediturno.Backend.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitaRepository extends JpaRepository<Cita, Long> {
}