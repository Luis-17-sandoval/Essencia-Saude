
package com.mediturno.Backend.controller;

import com.mediturno.Backend.model.Usuario;
import com.mediturno.Backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, String>> registrar(
            @RequestBody RegistroRequest datos) {

        Map<String, String> respuesta = new HashMap<>();

        if (datos.getNombre() == null || datos.getNombre().isBlank()
                || datos.getApellidoPaterno() == null
                || datos.getApellidoPaterno().isBlank()
                || datos.getApellidoMaterno() == null
                || datos.getApellidoMaterno().isBlank()
                || datos.getCorreo() == null
                || datos.getCorreo().isBlank()
                || datos.getCelular() == null
                || datos.getCelular().isBlank()
                || datos.getContrasena() == null
                || datos.getContrasena().isBlank()
                || datos.getFechaNacimiento() == null) {

            respuesta.put("mensaje",
                    "Completa todos los campos obligatorios.");

            return ResponseEntity.badRequest().body(respuesta);
        }

        String correo = datos.getCorreo().trim().toLowerCase();

        if (usuarioRepository.existsByCorreo(correo)) {
            respuesta.put("mensaje",
                    "Este correo ya está registrado.");

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(respuesta);
        }

        try {
            Usuario usuario = new Usuario();

            usuario.setNombres(datos.getNombre().trim());

            usuario.setApellidos(
                    datos.getApellidoPaterno().trim()
                    + " "
                    + datos.getApellidoMaterno().trim()
            );

            usuario.setCorreo(correo);
            usuario.setTelefono(datos.getCelular().trim());
            usuario.setFechaNacimiento(datos.getFechaNacimiento());
            usuario.setGenero(datos.getGenero());

            // Nunca guardar la contraseña en texto plano.
            usuario.setPasswordHash(
                    passwordEncoder.encode(datos.getContrasena())
            );

            // El rol PACIENTE se conserva como valor predeterminado
            // de la entidad y no se recibe desde el frontend.
            usuarioRepository.save(usuario);

            respuesta.put("mensaje", "Cuenta creada correctamente.");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(respuesta);

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            respuesta.put("mensaje",
                    "No se pudo registrar el usuario. "
                    + "Comprueba si el correo ya existe.");

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(respuesta);
        }
    }

    public static class RegistroRequest {

        private String nombre;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private java.time.LocalDate fechaNacimiento;
        private String genero;
        private String correo;
        private String celular;
        private String contrasena;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellidoPaterno() {
            return apellidoPaterno;
        }

        public void setApellidoPaterno(String apellidoPaterno) {
            this.apellidoPaterno = apellidoPaterno;
        }

        public String getApellidoMaterno() {
            return apellidoMaterno;
        }

        public void setApellidoMaterno(String apellidoMaterno) {
            this.apellidoMaterno = apellidoMaterno;
        }

        public java.time.LocalDate getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(
                java.time.LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public String getGenero() {
            return genero;
        }

        public void setGenero(String genero) {
            this.genero = genero;
        }

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getCelular() {
            return celular;
        }

        public void setCelular(String celular) {
            this.celular = celular;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> iniciarSesion(
            @RequestBody LoginRequest datos) {

        Map<String, Object> respuesta = new HashMap<>();

        if (datos.getCorreo() == null
                || datos.getCorreo().isBlank()
                || datos.getContrasena() == null
                || datos.getContrasena().isBlank()) {

            respuesta.put("mensaje",
                    "Ingresa tu correo y contraseña.");

            return ResponseEntity.badRequest().body(respuesta);
        }

        String correo = datos.getCorreo().trim().toLowerCase();

        var usuarioOptional = usuarioRepository.findByCorreo(correo);

        if (usuarioOptional.isEmpty()) {
            respuesta.put("mensaje",
                    "Correo o contraseña incorrectos.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(respuesta);
        }

        Usuario usuario = usuarioOptional.get();

        if (usuario.getPasswordHash() == null
                || !passwordEncoder.matches(
                        datos.getContrasena(),
                        usuario.getPasswordHash())) {

            respuesta.put("mensaje",
                    "Correo o contraseña incorrectos.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(respuesta);
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            respuesta.put("mensaje", "La cuenta está desactivada.");

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(respuesta);
        }

        respuesta.put("mensaje", "Inicio de sesión correcto.");
        respuesta.put("id", usuario.getId());
        respuesta.put("nombres", usuario.getNombres());
        respuesta.put("apellidos", usuario.getApellidos());
        respuesta.put("correo", usuario.getCorreo());
        respuesta.put("rol", usuario.getRol());

        return ResponseEntity.ok(respuesta);
    }

    public static class LoginRequest {

        private String correo;
        private String contrasena;

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }
    }

}
