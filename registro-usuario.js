
const API_URL = "http://localhost:8080/api/usuarios/registro";

const formulario = document.getElementById("form-registro");
const mensaje = document.getElementById("mensaje-registro");
const botonRegistrar = document.getElementById("btn-registrar");

formulario.addEventListener("submit", async (event) => {
    event.preventDefault();

    mensaje.textContent = "";
    mensaje.className = "form-message";

    if (!formulario.reportValidity()) {
        return;
    }

    const nombre = document.getElementById("nombre").value.trim();
    const correo = document.getElementById("correo").value.trim();
    const telefono = document.getElementById("telefono").value.trim();
    const contrasena = document.getElementById("contrasena").value;
    const confirmarContrasena =
        document.getElementById("confirmar-contrasena").value;

    if (contrasena !== confirmarContrasena) {
        mostrarMensaje("Las contraseñas no coinciden.", "error");
        return;
    }

    if (!document.getElementById("acepta-privacidad").checked) {
        mostrarMensaje(
            "Debes aceptar la política de privacidad.",
            "error"
        );
        return;
    }

    botonRegistrar.disabled = true;
    botonRegistrar.textContent = "Registrando...";

    try {
        const respuesta = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                nombre,
                correo,
                telefono: telefono || null,
                contrasena
            })
        });

        const resultado = await respuesta.json().catch(() => ({}));

        if (!respuesta.ok) {
            throw new Error(
                resultado.mensaje ||
                resultado.message ||
                "No se pudo crear la cuenta."
            );
        }

        mostrarMensaje(
            "Cuenta creada correctamente. Ahora inicia sesión para reservar.",
            "success"
        );

        formulario.reset();

        // No se inicia sesión automáticamente.
        // Primero se confirma el registro y luego se accede al login.
        setTimeout(() => {
            window.location.href = "iniciar-sesion.html";
        }, 1500);

    } catch (error) {
        mostrarMensaje(
            error.message ||
            "No se pudo conectar con el servidor. Inténtalo nuevamente.",
            "error"
        );
    } finally {
        botonRegistrar.disabled = false;
        botonRegistrar.textContent = "Crear cuenta";
    }
});

function mostrarMensaje(texto, tipo) {
    mensaje.textContent = texto;
    mensaje.className = `form-message ${tipo}`;
}
