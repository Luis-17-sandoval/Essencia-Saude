package com.mediturno.Backend.controller;

import com.mediturno.Backend.model.Medico;
import com.mediturno.Backend.repository.MedicoRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoRepository medicoRepository;

    public MedicoController(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @GetMapping
    public ResponseEntity<?> listarMedicos(
            @RequestParam(required = false) Long especialidadId) {

        List<Medico> medicos;

        if (especialidadId != null) {
            medicos = medicoRepository
                    .findByEspecialidad_IdAndActivoTrue(especialidadId);
        } else {
            medicos = medicoRepository.findAll()
                    .stream()
                    .filter(m -> Boolean.TRUE.equals(m.getActivo()))
                    .toList();
        }

        List<Map<String, Object>> resultado = medicos.stream()
                .map(m -> Map.<String, Object>of(
                        "id", m.getId(),
                        "nombres", m.getNombres(),
                        "apellidos", m.getApellidos(),
                        "especialidadId", m.getEspecialidad().getId()
                ))
                .toList();

        return ResponseEntity.ok(resultado);
    }
}
