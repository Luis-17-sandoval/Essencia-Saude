
package com.mediturno.Backend.repository;

import com.mediturno.Backend.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByMedico_IdAndDisponibleTrueAndMedico_ActivoTrueOrderByFechaAscHoraInicioAsc(
            Long medicoId
    );
}
