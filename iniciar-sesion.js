
const API_URL = "http://localhost:8080/api/usuarios/login";

const formulario = document.getElementById("form-login");
const mensaje = document.getElementById("mensaje-login");
const botonLogin = document.getElementById("btn-login");

const campoCorreo = document.getElementById("correo");
const campoContrasena = document.getElementById("contrasena");
const botonMostrar = document.getElementById("btn-mostrar-contrasena");

// Mostrar u ocultar contraseña.
botonMostrar.addEventListener("click", () => {
    const mostrar = campoContrasena.type === "password";

    campoContrasena.type = mostrar ? "text" : "password";
    botonMostrar.textContent = mostrar ? "Ocultar" : "Mostrar";
    botonMostrar.setAttribute(
        "aria-label",
        mostrar ? "Ocultar contraseña" : "Mostrar contraseña"
    );
    botonMostrar.setAttribute("aria-pressed", String(mostrar));
});

// Enviar formulario de inicio de sesión.
formulario.addEventListener("submit", async (event) => {
    event.preventDefault();

    mensaje.textContent = "";
    mensaje.className = "form-message";

    if (!formulario.reportValidity()) {
        return;
    }

    const correo = campoCorreo.value.trim();
    const contrasena = campoContrasena.value;

    if (!correo || !contrasena) {
        mostrarMensaje(
            "Ingresa tu correo electrónico y contraseña.",
            "error"
        );
        return;
    }

    botonLogin.disabled = true;
    botonLogin.textContent = "Iniciando sesión...";

    try {
        const respuesta = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({
                correo: correo,
                contrasena: contrasena
            })
        });

        const resultado = await respuesta.json().catch(() => ({}));

        if (!respuesta.ok) {
            throw new Error(
                resultado.mensaje ||
                resultado.message ||
                resultado.detail ||
                (
                    respuesta.status === 401
                        ? "Correo o contraseña incorrectos."
                        : "No se pudo iniciar sesión."
                )
            );
        }

        mostrarMensaje(
            "Inicio de sesión exitoso. Redirigiendo...",
            "success"
        );

        // Guarda únicamente los datos públicos necesarios para identificar al usuario.
        // La contraseña nunca se almacena en el navegador.
        const usuario = {
            id: resultado.id,
            nombres: resultado.nombres,
            apellidos: resultado.apellidos,
            correo: resultado.correo,
            rol: resultado.rol
        };

        localStorage.setItem(
            "mediturno_usuario",
            JSON.stringify(usuario)
        );

        if (usuario.rol === "ADMIN") {
            window.location.href = "dashboard.html";
        } else if (usuario.rol === "PACIENTE") {
            window.location.href = "registrar_cita.html";
        } else {
            localStorage.removeItem("mediturno_usuario");
            mostrarMensaje(
                "Tu cuenta no tiene un rol autorizado.",
                "error"
            );
        }

    } catch (error) {
        mostrarMensaje(
            error.message ||
            "No se pudo conectar con el servidor. Inténtalo nuevamente.",
            "error"
        );

    } finally {
        botonLogin.disabled = false;
        botonLogin.textContent = "Iniciar sesión";
    }
});

function mostrarMensaje(texto, tipo) {
    mensaje.textContent = texto;
    mensaje.className = `form-message ${tipo}`;
}
