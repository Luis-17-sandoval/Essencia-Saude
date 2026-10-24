
let appointments = [];

const specialties = [
    { name: "Medicina general", value: 22 },
    { name: "Pediatría", value: 18 },
    { name: "Ginecología", value: 15 },
    { name: "Cardiología", value: 12 },
    { name: "Dermatología", value: 9 },
    { name: "Otras", value: 6 }
];

const $ = (selector) => document.querySelector(selector);

function showDate() {
    const now = new Date();

    $("#current-date").textContent = now.toLocaleDateString("es-PE", {
        weekday: "long",
        day: "numeric",
        month: "long",
        year: "numeric"
    });
}

function renderChart() {
    const chart = $("#chart");
    chart.replaceChildren();

    const maxValue = Math.max(...specialties.map(item => item.value));

    specialties.forEach(item => {
        const group = document.createElement("div");
        group.className = "bar-group";

        const bar = document.createElement("div");
        bar.className = "bar";
        bar.style.height = `${(item.value / maxValue) * 76}%`;
        bar.title = `${item.name}: ${item.value} citas`;

        const value = document.createElement("span");
        value.className = "bar-value";
        value.textContent = item.value;
        bar.append(value);

        const label = document.createElement("div");
        label.className = "bar-label";
        label.textContent = item.name;

        group.append(bar, label);
        chart.append(group);
    });
}

function updateStatistics() {
    const confirmed = appointments.filter(a => a.status === "Confirmada").length;
    const pending = appointments.filter(a => a.status === "Pendiente").length;
    const cancelled = appointments.filter(a => a.status === "Cancelada").length;

    $("#total-appointments").textContent = appointments.length;
    $("#total-pending").textContent = pending;
    $("#confirmed-count").textContent = confirmed;
    $("#pending-count").textContent = pending;
    $("#cancelled-count").textContent = cancelled;

    const total = appointments.length || 1;

    document.querySelector(".progress span:not(.yellow-bar):not(.red-bar)")
        .style.width = `${confirmed / total * 100}%`;

    $(".yellow-bar").style.width = `${pending / total * 100}%`;
    $(".red-bar").style.width = `${cancelled / total * 100}%`;
}


function renderAppointments(searchText = "") {
    const tbody = $("#appointments-body");
    tbody.replaceChildren();

    const query = searchText.trim().toLocaleLowerCase("es");

    const filtered = appointments.filter(a =>
        [
            a.patient,
            a.doctor,
            a.specialty,
            a.date,
            a.time,
            a.status
        ].some(value =>
            String(value ?? "").toLocaleLowerCase("es").includes(query)
        )
    );

    filtered.forEach(appointment => {
        const row = document.createElement("tr");

        const values = [
            appointment.patient,
            appointment.doctor,
            appointment.specialty,
            `${appointment.date} - ${appointment.time}`
        ];

        values.forEach((value, index) => {
            const cell = document.createElement("td");
            cell.textContent = value ?? "";

            if (index === 0) {
                cell.className = "patient-name";
            }

            row.appendChild(cell);
        });

        const statusCell = document.createElement("td");
        const badge = document.createElement("span");

        badge.className =
            `status status-${appointment.status.toLowerCase()}`;
        badge.textContent = appointment.status;

        statusCell.appendChild(badge);
        row.appendChild(statusCell);

        const actionCell = document.createElement("td");
        const button = document.createElement("button");

        button.className = "action-button";

        if (appointment.status === "Cancelada") {
            button.textContent = "Cancelada";
            button.disabled = true;
        } else {
            button.textContent = "Cancelar";
            button.addEventListener("click", () => {
                cancelAppointment(appointment.id);
            });
        }

        actionCell.appendChild(button);
        row.appendChild(actionCell);
        tbody.appendChild(row);
    });

    $("#empty-message").hidden = filtered.length > 0;
}


function cancelAppointment(id) {
    const appointment = appointments.find(a => a.id === id);

    if (!appointment || appointment.status === "Cancelada") {
        return;
    }

    const accepted = confirm(
        `¿Deseas cancelar la cita de ${appointment.patient}?`
    );

    if (!accepted) {
        return;
    }

    appointment.status = "Cancelada";

    updateStatistics();
    renderAppointments($("#search").value);

    const notice = $("#notice");
    notice.textContent = "La cita de demostración se ha cancelado correctamente.";
    notice.hidden = false;
}

function setupNavigation() {
    document.querySelectorAll("[data-page]").forEach(button => {
        button.addEventListener("click", () => {
            const page = button.dataset.page;

            document.querySelectorAll(".menu-item").forEach(item => {
                item.classList.toggle("active", item.dataset.page === page);
            });

            $("#page-title").textContent =
                page === "Inicio" ? "¡Hola, administrador!" : page;

            if (page !== "Inicio") {
                $("#notice").textContent =
                    `El módulo "${page}" está pendiente de implementación.`;
                $("#notice").hidden = false;
            } else {
                $("#notice").hidden = true;
            }

            $(".sidebar").classList.remove("open");
        });
    });
}

function init() {
    showDate();
    renderChart();
    updateStatistics();
    renderAppointments();
    cargarCitasDesdeBackend();
    setupNavigation();

    $("#search").addEventListener("input", event => {
        renderAppointments(event.target.value);
    });

    $("#menu-toggle").addEventListener("click", () => {
        $(".sidebar").classList.toggle("open");
    });
}

document.addEventListener("DOMContentLoaded", init);


const API_CITAS = "http://localhost:8080/api/citas";

async function cargarCitasDesdeBackend() {
    try {
        const respuesta = await fetch(API_CITAS);

        if (!respuesta.ok) {
            throw new Error("Error HTTP: " + respuesta.status);
        }

        const citasBackend = await respuesta.json();

        // Adaptar los nombres de Java a los que usa el frontend
        appointments = citasBackend.map(cita => ({
            id: cita.id,
            patient: cita.paciente,
            doctor: cita.medico,
            specialty: cita.especialidad,
            date: cita.fecha,
            time: String(cita.hora).slice(0, 5),
            status: cita.estado
        }));

        updateStatistics();
        renderAppointments($("#search").value);

        console.log("Citas cargadas desde MySQL:", appointments.length);

    } catch (error) {
        console.error("Error al cargar las citas:", error);

        $("#empty-message").hidden = false;
        $("#empty-message").textContent =
            "No se pudieron cargar las citas. Verifica que el backend esté activo.";
    }
}

