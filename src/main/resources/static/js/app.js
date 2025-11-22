// ----------- GESTIÓN DE PROYECTOS (API REST) -----------

// URL para la API REST de proyectos (cambia si usas otro endpoint)
const API_URL = "http://localhost:8080/api/v1/proyectos";

// Cargar lista de proyectos (solo si elemento existe)
async function cargarProyectos() {
    const lista = document.getElementById("listaProyectos");
    if (!lista) return;
    const res = await fetch(API_URL);
    const proyectos = await res.json();

    lista.innerHTML = "";
    proyectos.forEach(p => {
        lista.innerHTML += `
            <div class="col-md-4 mb-3">
                <div class="card shadow-sm p-3 card-hover">
                    <h5>${p.nombre}</h5>
                    <p>${p.descripcion}</p>
                    <small class="text-muted">
                        Creado: ${new Date(p.fechaCreacion).toLocaleDateString()}
                    </small>
                    <div class="mt-3 d-flex gap-2">
                        <button class="btn btn-info btn-sm" onclick="verTareas(${p.id})">Ver Tareas</button>
                        <button class="btn btn-danger btn-sm" onclick="eliminarProyecto(${p.id})">Eliminar</button>
                    </div>
                </div>
            </div>
        `;
    });
}

// Crear proyecto nuevo
async function crearProyecto() {
    const nombre = document.getElementById("nombre").value;
    const descripcion = document.getElementById("descripcion").value;

    await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nombre, descripcion })
    });

    document.getElementById("nombre").value = "";
    document.getElementById("descripcion").value = "";

    cargarProyectos();
}

// Navega a la vista de tareas con parámetro
function verTareas(id) {
    window.location.href = `tareas.html?proyecto=${id}`;
}

// Eliminar proyecto
async function eliminarProyecto(id) {
    if (!confirm("¿Eliminar proyecto?")) return;
    await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    cargarProyectos();
}

// ----------- CONFIRMACIÓN DE BORRADO UNIVERSAL -----------

document.addEventListener("DOMContentLoaded", function() {
    const forms = document.querySelectorAll("form[action*='/eliminar']");
    forms.forEach(form => {
        form.addEventListener("submit", function(e) {
            if (!confirm("¿Eliminar elemento?")) {
                e.preventDefault();
            }
        });
    });
});

// ----------- DASHBOARD DE TAREAS (CHART.JS) -----------

document.addEventListener("DOMContentLoaded", function() {
    // Gráfico de prioridades
    if (typeof Chart !== 'undefined' && document.getElementById('prioridadChart')) {
        new Chart(document.getElementById('prioridadChart'), {
            type: 'pie',
            data: {
                labels: ['Alta', 'Media', 'Baja'],
                datasets: [{
                    label: 'Prioridad',
                    data: [prioridadAlta, prioridadMedia, prioridadBaja],
                    backgroundColor: ['#dd3a53', '#f2bc41', '#37bbb2']
                }]
            }
        });
    }

    // Gráfico de estados
    if (typeof Chart !== 'undefined' && document.getElementById('estadoChart')) {
        new Chart(document.getElementById('estadoChart'), {
            type: 'doughnut',
            data: {
                labels: ['Pendiente', 'En Progreso', 'Completada'],
                datasets: [{
                    label: 'Estado',
                    data: [tareasPendientes, tareasEnProgreso, tareasCompletadas],
                    backgroundColor: ['#f2bc41', '#2196f3', '#4caf50']
                }]
            }
        });
    }

    // ----------- FULLCALENDAR (VISTA DÍA + MODAL DETALLE) -----------

    const calendarEl = document.getElementById('calendar');
    if (calendarEl && typeof FullCalendar !== 'undefined') {
        const calendar = new FullCalendar.Calendar(calendarEl, {
            // Vista inicial por día; puedes cambiar a 'dayGridDay' si no quieres horas [web:29][web:33]
            initialView: 'timeGridDay',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridDay,timeGridWeek,dayGridMonth'
            },
            events: calendarEvents.map(t => ({
                title: t.titulo + (t.responsable ? " (" + t.responsable.nombre + ")" : ""),
                start: t.fechaLimite,
                color: t.estado === 'Completada' ? '#4caf50'
                      : (t.estado === 'En progreso' ? '#2196f3' : '#f2bc41'),
                extendedProps: {
                    descripcion: t.descripcion,
                    prioridad: t.prioridad,
                    estado: t.estado
                }
            })),
            eventClick: function(info) {
                // Llenar modal con detalles de la tarea [web:2][web:58]
                document.getElementById("detalleTareaTitulo").textContent =
                    info.event.title;
                document.getElementById("detalleTareaDescripcion").textContent =
                    info.event.extendedProps.descripcion || "";
                document.getElementById("detalleTareaPrioridad").textContent =
                    info.event.extendedProps.prioridad || "";
                document.getElementById("detalleTareaEstado").textContent =
                    info.event.extendedProps.estado || "";

                const fecha = new Date(info.event.start);
                document.getElementById("detalleTareaFecha").textContent =
                    fecha.toLocaleDateString();

                const modal = new bootstrap.Modal(
                    document.getElementById('detalleTareaModal')
                );
                modal.show();

                info.jsEvent.preventDefault();
            }
        });

        calendar.render();
    }
});
