
package com.mediturno.Backend.repository;

import com.mediturno.Backend.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EspecialidadRepository
        extends JpaRepository<Especialidad, Long> {

    List<Especialidad> findByActivaTrue();
}
