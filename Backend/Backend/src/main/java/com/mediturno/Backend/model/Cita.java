package com.mediturno.Backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paciente;

    @Column(nullable = false)
    private String medico;

    @Column(nullable = false)
    private String especialidad;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false)
    private String estado;

    public Cita() {
    }

    public Cita(String paciente, String medico,
                String especialidad, LocalDate fecha,
                LocalTime hora, String estado) {
        this.paciente = paciente;
        this.medico = medico;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    public Long getId() { return id; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getMedico() { return medico; }
    public void setMedico(String medico) {
        this.medico = medico;
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}