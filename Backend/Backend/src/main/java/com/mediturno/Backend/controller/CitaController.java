
package com.mediturno.Backend.controller;

import com.mediturno.Backend.model.Cita;
import com.mediturno.Backend.model.Usuario;
import com.mediturno.Backend.model.Horario;
import com.mediturno.Backend.dto.CitaRespuesta;
import com.mediturno.Backend.repository.CitaRepository;
import com.mediturno.Backend.repository.UsuarioRepository;
import com.mediturno.Backend.repository.HorarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500"
})
public class CitaController {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioRepository horarioRepository;

    public CitaController(
            CitaRepository citaRepository,
            UsuarioRepository usuarioRepository,
            HorarioRepository horarioRepository) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.horarioRepository = horarioRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<CitaRespuesta> listarCitas() {
        return citaRepository.findAll()
                .stream()
                .map(CitaRespuesta::desde)
                .toList();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> registrarCita(@RequestBody Map<String, Object> datos) {
        try {
            Object pacienteId = datos.get("pacienteId");
            Object horarioId = datos.get("horarioId");

            if (pacienteId == null || horarioId == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Debes enviar pacienteId y horarioId."));
            }

            Long idPaciente = Long.valueOf(pacienteId.toString());
            Long idHorario = Long.valueOf(horarioId.toString());

            Usuario usuario = usuarioRepository.findById(idPaciente)
                    .orElse(null);

            if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "El paciente no existe o está inactivo."));
            }

            Horario horario = horarioRepository.findById(idHorario)
                    .orElse(null);

            if (horario == null || !Boolean.TRUE.equals(horario.getDisponible())) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "El horario no existe o ya no está disponible."));
            }

            Cita cita = new Cita();
            cita.setUsuario(usuario);
            cita.setHorario(horario);
            cita.setEstado("PENDIENTE");

            if (datos.get("motivo") != null) {
                cita.setMotivo(datos.get("motivo").toString());
            }

            Cita guardada = citaRepository.save(cita);

            horario.setDisponible(false);
            horarioRepository.save(horario);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "mensaje", "Cita registrada correctamente.",
                            "id", guardada.getId(),
                            "estado", guardada.getEstado()));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Los identificadores deben ser números válidos."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "No se pudo registrar la cita. Revisa la consola del backend."));
        }
    }
}
