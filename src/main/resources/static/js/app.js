const API_URL = "http://localhost:8080/api/v1/proyectos";

// ✅ Cargar proyectos
async function cargarProyectos() {
    const res = await fetch(API_URL);
    const proyectos = await res.json();

    const lista = document.getElementById("listaProyectos");
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

// ✅ Crear proyecto
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

// ✅ Ir a tareas
function verTareas(id){
    window.location.href = `tareas.html?proyecto=${id}`;
}

// ✅ Eliminar proyecto
async function eliminarProyecto(id){
    if(!confirm("¿Eliminar proyecto?")) return;
    
    await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    cargarProyectos();
}

document.addEventListener("DOMContentLoaded", cargarProyectos);
