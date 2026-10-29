
const API_URL = "http://localhost:8080/api/usuarios/registro";

const formulario = document.getElementById("form-registro");
const mensaje = document.getElementById("mensaje-registro");
const botonRegistrar = document.getElementById("btn-registrar");

const campoFechaNacimiento = document.getElementById("fechaNacimiento");

// Evitar que se pueda seleccionar una fecha futura.
const hoy = new Date();
const anio = hoy.getFullYear();
const mes = String(hoy.getMonth() + 1).padStart(2, "0");
const dia = String(hoy.getDate()).padStart(2, "0");

campoFechaNacimiento.max = `${anio}-${mes}-${dia}`;

formulario.addEventListener("submit", async (event) => {
    event.preventDefault();

    mensaje.textContent = "";
    mensaje.className = "form-message";

    // Validaciones HTML: campos requeridos, email y longitudes.
    if (!formulario.reportValidity()) {
        return;
    }

    // Obtener y limpiar los datos del formulario.
    const nombre = document.getElementById("nombre").value.trim();

    const apellidoPaterno = document
        .getElementById("apellidoPaterno")
        .value.trim();

    const apellidoMaterno = document
        .getElementById("apellidoMaterno")
        .value.trim();

    const fechaNacimiento = campoFechaNacimiento.value;

    const genero = document.getElementById("genero").value;

    const correo = document.getElementById("correo")
        .value.trim();

    const celular = document.getElementById("celular")
        .value.trim();

    const contrasena = document.getElementById("contrasena")
        .value;

    const confirmarContrasena = document
        .getElementById("confirmar-contrasena")
        .value;

    const aceptaPrivacidad = document
        .getElementById("acepta-privacidad")
        .checked;

    // Comprobar que los nombres no estén formados solo por espacios.
    if (
        !nombre ||
        !apellidoPaterno ||
        !apellidoMaterno ||
        !correo ||
        !celular
    ) {
        mostrarMensaje(
            "Completa todos los campos obligatorios.",
            "error"
        );
        return;
    }

    // Comprobar que la fecha sea válida y no sea futura.
    if (
        !fechaNacimiento ||
        fechaNacimiento > campoFechaNacimiento.max
    ) {
        mostrarMensaje(
            "Ingresa una fecha de nacimiento válida.",
            "error"
        );
        return;
    }

    // Verificar que las contraseñas coincidan.
    if (contrasena !== confirmarContrasena) {
        mostrarMensaje(
            "Las contraseñas no coinciden.",
            "error"
        );
        document.getElementById("confirmar-contrasena").focus();
        return;
    }

    // Verificar aceptación de la política de privacidad.
    if (!aceptaPrivacidad) {
        mostrarMensaje(
            "Debes aceptar la política de privacidad.",
            "error"
        );
        return;
    }

    // Evitar envíos repetidos.
    botonRegistrar.disabled = true;
    botonRegistrar.textContent = "Registrando...";

    try {
        const respuesta = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({
                nombre: nombre,
                apellidoPaterno: apellidoPaterno,
                apellidoMaterno: apellidoMaterno,
                fechaNacimiento: fechaNacimiento,
                genero: genero,
                correo: correo,
                celular: celular,
                contrasena: contrasena
            })
        });

        const resultado = await respuesta.json().catch(() => ({}));

        if (!respuesta.ok) {
            throw new Error(
                resultado.mensaje ||
                resultado.message ||
                resultado.detail ||
                "No se pudo crear la cuenta."
            );
        }

        mostrarMensaje(
            "Cuenta creada correctamente. Redirigiendo al inicio de sesión...",
            "success"
        );

        formulario.reset();

        // Restablecer la fecha máxima después de limpiar el formulario.
        campoFechaNacimiento.max = `${anio}-${mes}-${dia}`;

        // No iniciar sesión automáticamente.
        setTimeout(() => {
            window.location.href = "login.html";
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
