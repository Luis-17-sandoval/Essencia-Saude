
package com.mediturno.Backend.dto;

import com.mediturno.Backend.model.Cita;
import java.time.LocalDate;
import java.time.LocalTime;

public record CitaRespuesta(
    Long id,
    String paciente,
    String medico,
    String especialidad,
    LocalDate fecha,
    LocalTime hora,
    String estado
) {
    public static CitaRespuesta desde(Cita cita) {
        return new CitaRespuesta(
            cita.getId(),
            cita.getPaciente(),
            cita.getMedico(),
            cita.getEspecialidad(),
            cita.getFecha(),
            cita.getHora(),
            cita.getEstado()
        );
    }
}
