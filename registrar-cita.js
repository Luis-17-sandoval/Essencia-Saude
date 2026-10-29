const API = "http://localhost:8080/api";
const formulario = document.getElementById("form-cita");
const mensaje = document.getElementById("mensaje-formulario");
const selectEspecialidad = document.getElementById("especialidad");
const selectMedico = document.getElementById("medico");
const selectHorario = document.getElementById("horario");
const usuario = JSON.parse(localStorage.getItem("mediturno_usuario") || "null");

function mostrarMensaje(texto, tipo = "error") {
    mensaje.textContent = texto;
    mensaje.className = `registro-message ${tipo}`;
    mensaje.hidden = false;
}
function llenarSelect(select, opciones, textoInicial, labelFn, valueFn) {
    select.replaceChildren();
    const inicial = document.createElement("option");
    inicial.value = ""; inicial.textContent = textoInicial; select.append(inicial);
    opciones.forEach(item => {
        const option = document.createElement("option");
        option.value = valueFn(item); option.textContent = labelFn(item); select.append(option);
    });
}
async function obtener(url) {
    const respuesta = await fetch(url);
    if (!respuesta.ok) throw new Error(`No se pudieron cargar los datos (HTTP ${respuesta.status}).`);
    return respuesta.json();
}

async function cargarEspecialidades() {
    const datos = await obtener(`${API}/especialidades`);
    llenarSelect(selectEspecialidad, datos, "Selecciona una especialidad", e => e.nombre, e => e.id);
}
selectEspecialidad.addEventListener("change", async () => {
    selectMedico.disabled = true; selectHorario.disabled = true;
    llenarSelect(selectMedico, [], "Cargando médicos...", d => d.nombre, d => d.id);
    llenarSelect(selectHorario, [], "Primero selecciona un médico", h => h.fecha, h => h.id);
    if (!selectEspecialidad.value) return;
    try {
        const datos = await obtener(`${API}/medicos?especialidadId=${encodeURIComponent(selectEspecialidad.value)}`);
        llenarSelect(selectMedico, datos, "Selecciona un médico", d => d.nombre, d => d.id);
        selectMedico.disabled = datos.length === 0;
        if (!datos.length) mostrarMensaje("No hay médicos activos para esta especialidad.");
    } catch (e) { mostrarMensaje(e.message); }
});
selectMedico.addEventListener("change", async () => {
    selectHorario.disabled = true;
    llenarSelect(selectHorario, [], "Cargando horarios...", h => h.label, h => h.id);
    if (!selectMedico.value) return;
    try {
        const datos = await obtener(`${API}/horarios?medicoId=${encodeURIComponent(selectMedico.value)}`);
        const opciones = datos.map(h => ({...h, label: `${h.fecha} — ${String(h.hora).slice(0,5)}`}));
        llenarSelect(selectHorario, opciones, "Selecciona fecha y hora", h => h.label, h => h.id);
        selectHorario.disabled = opciones.length === 0;
        if (!opciones.length) mostrarMensaje("Este médico no tiene horarios disponibles. Selecciona otro.", "error");
    } catch (e) { mostrarMensaje(e.message); }
});

formulario.addEventListener("submit", async event => {
    event.preventDefault();
    mensaje.hidden = true;
    if (!usuario?.id) {
        mostrarMensaje("Debes iniciar sesión antes de reservar una cita.");
        setTimeout(() => window.location.href = "login.html", 900);
        return;
    }
    if (!formulario.reportValidity()) return;
    const boton = formulario.querySelector('button[type="submit"]');
    boton.disabled = true; boton.textContent = "Registrando cita...";
    try {
        const respuesta = await fetch(`${API}/citas`, {
            method: "POST", headers: {"Content-Type":"application/json", "Accept":"application/json"},
            body: JSON.stringify({ pacienteId: usuario.id, horarioId: Number(selectHorario.value), motivo: document.getElementById("motivo").value.trim() })
        });
        const resultado = await respuesta.json().catch(() => ({}));
        if (!respuesta.ok) throw new Error(resultado.message || resultado.mensaje || resultado.detail || `No se pudo registrar la cita (HTTP ${respuesta.status}).`);
        mostrarMensaje("¡Cita registrada correctamente! Tu reserva está pendiente de confirmación.", "success");
        formulario.reset(); selectMedico.disabled = true; selectHorario.disabled = true;
        await cargarEspecialidades();
    } catch (e) { mostrarMensaje(e.message || "No se pudo conectar con el servidor."); }
    finally { boton.disabled = false; boton.textContent = "Registrar cita"; }
});

document.addEventListener("DOMContentLoaded", async () => {
    if (!usuario?.id) {
        document.getElementById("usuario-conectado").textContent = "Para reservar, inicia sesión o crea una cuenta.";
        mostrarMensaje("Necesitas iniciar sesión para reservar una cita.");
        setTimeout(() => window.location.href = "login.html", 1200);
        return;
    }
    document.getElementById("usuario-conectado").textContent = `Paciente: ${usuario.nombres} ${usuario.apellidos}`;
    try { await cargarEspecialidades(); }
    catch (e) { mostrarMensaje(`${e.message} Verifica que Spring Boot esté ejecutándose.`); }
});
