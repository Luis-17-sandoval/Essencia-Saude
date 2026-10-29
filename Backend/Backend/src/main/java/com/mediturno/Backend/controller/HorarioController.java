
package com.mediturno.Backend.controller;

import com.mediturno.Backend.model.Horario;
import com.mediturno.Backend.repository.HorarioRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://127.0.0.1:5500"
})
public class HorarioController {

    private final HorarioRepository horarioRepository;

    public HorarioController(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public record HorarioDTO(
        Long id,
        LocalDate fecha,
        LocalTime hora
    ) {}

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<HorarioDTO>> listarHorarios(
            @RequestParam Long medicoId) {

        List<HorarioDTO> horarios = horarioRepository
            .findByMedico_IdAndDisponibleTrueAndMedico_ActivoTrueOrderByFechaAscHoraInicioAsc(
                medicoId
            )
            .stream()
            .map(h -> new HorarioDTO(
                h.getId(),
                h.getFecha(),
                h.getHoraInicio()
            ))
            .toList();

        return ResponseEntity.ok(horarios);
    }
}
