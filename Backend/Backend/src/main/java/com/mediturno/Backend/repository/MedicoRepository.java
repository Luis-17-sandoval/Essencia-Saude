package com.mediturno.Backend.repository;

import com.mediturno.Backend.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    List<Medico> findByEspecialidad_IdAndActivoTrue(Long especialidadId);
}
