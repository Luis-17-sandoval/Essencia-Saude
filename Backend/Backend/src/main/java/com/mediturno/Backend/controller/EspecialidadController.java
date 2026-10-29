package com.mediturno.Backend.controller;

import com.mediturno.Backend.model.Especialidad;
import com.mediturno.Backend.repository.EspecialidadRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadController(
            EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    @GetMapping
    public List<Map<String, Object>> listarEspecialidades() {
        return especialidadRepository.findByActivaTrue()
                .stream()
                .map(e -> Map.<String, Object>of(
                        "id", e.getId(),
                        "nombre", e.getNombre()
                ))
                .toList();
    }
}
