// ----------- API Rest de Proyectos -----------

// Cambia URL si tu backend es distinto.
const API_URL = "http://localhost:8080/api/v1/proyectos";

// Carga lista de proyectos (opcional, para otras vistas)
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

// Crear proyecto nuevo (opcional, según tu flujo)
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

// Navegar a vista por tareas de proyecto
function verTareas(id) {
    window.location.href = `tareas.html?proyecto=${id}`;
}

// Eliminar un proyecto (opcional, según tu flujo)
async function eliminarProyecto(id) {
    if (!confirm("¿Eliminar proyecto?")) return;
    await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    cargarProyectos();
}

// ----------- Confirmación universal de borrado -----------

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

// ----------- DASHBOARD Y CALENDARIO -----------

document.addEventListener("DOMContentLoaded", function() {
    // Dashboard-Charts: Estadísticas de tareas
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

    // ----------- FULLCALENDAR dentro de pestaña -----------

    let calendar;
    const calendarioTabButton = document.getElementById('calendario-tab');
    if(calendarioTabButton){
        calendarioTabButton.addEventListener('shown.bs.tab', function () {
            if (!calendar) {
                const calendarEl = document.getElementById("calendar");
                if(!calendarEl) return;
                calendar = new FullCalendar.Calendar(calendarEl, {
                    initialView: 'dayGridMonth',
                    headerToolbar: {
                        left: 'prev,next today',
                        center: 'title',
                        right: 'dayGridDay,timeGridWeek,dayGridMonth'
                    },
                    events: Array.isArray(calendarEvents) ? calendarEvents.map(function(t) {
                        return {
                            title: t.titulo + (t.responsable && t.responsable.nombre ? " (" + t.responsable.nombre + ")" : ""),
                            start: t.fechaLimite ? t.fechaLimite : null,
                            url: "/tareas/" + t.id + "/detalle",
                            color: t.estado === "Completada" ? "#4caf50" :
                                (t.estado === "En progreso" ? "#2196f3" : "#f2bc41"),
                            extendedProps: {
                                descripcion: t.descripcion,
                                prioridad: t.prioridad,
                                estado: t.estado,
                                id: t.id
                            }
                        }
                    }) : [],
                    eventClick: function(info) {
                        // VENTANA MODAL de detalle (solo si tienes el modal en tu HTML)
                        if(document.getElementById("detalleTareaTitulo")){
                            document.getElementById("detalleTareaTitulo").textContent = info.event.title;
                            document.getElementById("detalleTareaDescripcion").textContent = info.event.extendedProps.descripcion || "";
                            document.getElementById("detalleTareaPrioridad").textContent = info.event.extendedProps.prioridad || "";
                            document.getElementById("detalleTareaEstado").textContent = info.event.extendedProps.estado || "";
                            const fecha = new Date(info.event.start);
                            document.getElementById("detalleTareaFecha").textContent = fecha.toLocaleDateString();
                            const modal = new bootstrap.Modal(document.getElementById('detalleTareaModal'));
                            modal.show();
                        }
                        info.jsEvent.preventDefault();
                    }
                });
                calendar.render();
            }
        });
    }
});
