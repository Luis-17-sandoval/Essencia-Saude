package com.mediturno.Backend.controller;

import com.mediturno.Backend.model.Cita;
import com.mediturno.Backend.repository.CitaRepository;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://127.0.0.1:5500"
})
public class CitaController {

    private final CitaRepository citaRepository;

    public CitaController(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @GetMapping
    public List<Cita> listarCitas() {
        return citaRepository.findAll();
    }
    @PostMapping
    public Cita registrarCita(@RequestBody Cita cita) {
    return citaRepository.save(cita);
    }
}