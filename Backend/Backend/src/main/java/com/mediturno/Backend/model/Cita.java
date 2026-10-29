
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

    // Relaciones con las tablas actuales
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;

    @Column(name = "estado", nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "motivo", length = 500)
    private String motivo;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    // Columnas antiguas: siguen siendo obligatorias en MySQL
    @Column(name = "paciente", nullable = false)
    private String paciente;

    @Column(name = "medico", nullable = false)
    private String medico;

    @Column(name = "especialidad", nullable = false)
    private String especialidad;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    public Cita() {
    }

    // Completa las columnas antiguas antes de guardar la cita

    @PrePersist
    @PreUpdate
    private void completarDatosAntiguos() {
        if (usuario == null || horario == null) {
            throw new IllegalStateException(
                    "La cita debe tener un paciente y un horario.");
        }

        String nombres = usuario.getNombres();
        String apellidos = usuario.getApellidos();

        if (nombres == null || apellidos == null
                || nombres.isBlank() || apellidos.isBlank()) {
            throw new IllegalStateException(
                    "El paciente debe tener nombres y apellidos.");
        }

        paciente = (nombres.trim() + " " + apellidos.trim()).trim();

        fecha = horario.getFecha();
        hora = horario.getHoraInicio();

        Medico doctor = horario.getMedico();

        if (doctor == null) {
            throw new IllegalStateException(
                    "El horario debe tener un médico asignado.");
        }

        medico = (doctor.getNombres() + " "
                + doctor.getApellidos()).trim();

        Especialidad esp = doctor.getEspecialidad();

        if (esp == null) {
            throw new IllegalStateException(
                    "El médico debe tener una especialidad.");
        }

        especialidad = esp.getNombre();
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getPaciente() {
        return paciente;
    }

    public String getMedico() {
        return medico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }
}
