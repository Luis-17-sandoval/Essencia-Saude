
const API_CITAS = "http://localhost:8080/api/citas";

const formulario = document.getElementById("form-cita");
const mensaje = document.getElementById("mensaje-formulario");

formulario.addEventListener("submit", async (event) => {
    event.preventDefault();

    const datos = new FormData(formulario);

    const cita = {
        paciente: datos.get("paciente").trim(),
        medico: datos.get("medico").trim(),
        especialidad: datos.get("especialidad").trim(),
        fecha: datos.get("fecha"),
        hora: datos.get("hora"),
        estado: "Pendiente"
    };

    try {
        const respuesta = await fetch(API_CITAS, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(cita)
        });

        if (!respuesta.ok) {
            throw new Error(`Error HTTP ${respuesta.status}`);
        }

        mensaje.textContent = "¡Cita registrada correctamente!";
        mensaje.hidden = false;
        formulario.reset();

    } catch (error) {
        console.error("Error al registrar la cita:", error);

        mensaje.textContent =
            "No se pudo registrar la cita. Comprueba que el backend esté activo.";
        mensaje.hidden = false;
    }
});
