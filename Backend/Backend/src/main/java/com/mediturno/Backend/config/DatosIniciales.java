
package com.mediturno.Backend.config;

import com.mediturno.Backend.model.Cita;
import com.mediturno.Backend.repository.CitaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarCitas(CitaRepository repositorio) {
        return args -> {
            if (repositorio.count() == 0) {
                LocalDate hoy = LocalDate.now();

                repositorio.save(new Cita(
                    "Maria Lopez", "Dr. Carlos Mendez",
                    "Medicina general", hoy,
                    LocalTime.of(9, 0), "Confirmada"
                ));

                repositorio.save(new Cita(
                    "Juan Perez", "Dra. Ana Torres",
                    "Pediatria", hoy,
                    LocalTime.of(10, 30), "Confirmada"
                ));

                repositorio.save(new Cita(
                    "Sofia Ramirez", "Dr. Luis Herrera",
                    "Ginecologia", hoy,
                    LocalTime.of(12, 0), "Pendiente"
                ));

                System.out.println("Citas de prueba registradas.");
            }
        };
    }
}
