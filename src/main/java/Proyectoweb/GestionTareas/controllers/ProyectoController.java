package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Proyecto;
import Proyectoweb.GestionTareas.models.Usuario;
import Proyectoweb.GestionTareas.Repositories.ProyectoRepository;
import Proyectoweb.GestionTareas.Repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/api/v1/proyectos")
@CrossOrigin(origins = "*")
public class ProyectoController {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    // ✅ Obtener todos los proyectos
    @GetMapping
    public List<Proyecto> getAllProyectos() {
        return proyectoRepository.findAll();
    }

    // ✅ Obtener proyecto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> getProyectoById(@PathVariable int id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));
        return ResponseEntity.ok(proyecto);
    }

    // ✅ Crear proyecto
    @PostMapping
    public Proyecto createProyecto(@RequestBody Proyecto proyecto) {
        proyecto.setFechaCreacion(new Date());
        return proyectoRepository.save(proyecto);
    }

    // ✅ Actualizar proyecto
    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> updateProyecto(@PathVariable int id, @RequestBody Proyecto detalles) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));

        proyecto.setNombre(detalles.getNombre());
        proyecto.setDescripcion(detalles.getDescripcion());

        Proyecto actualizado = proyectoRepository.save(proyecto);
        return ResponseEntity.ok(actualizado);
    }

    // ✅ Eliminar proyecto
    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteProyecto(@PathVariable int id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));

        proyectoRepository.delete(proyecto);
        Map<String, Boolean> response = new HashMap<>();
        response.put("eliminado", Boolean.TRUE);
        return response;
    }

    

@PostMapping("/proyectos")
public String crearProyecto(
        @RequestParam String nombre,
        @RequestParam String descripcion,
        @RequestParam(required = false) String correosParticipantes) {

    Proyecto p = new Proyecto();
    p.setNombre(nombre);
    p.setDescripcion(descripcion);

    // Procesar correos de participantes
    if (correosParticipantes != null && !correosParticipantes.trim().isEmpty()) {
        String[] correos = correosParticipantes.split(",");
        List<Usuario> participantes = new ArrayList<>();
        for (String correo : correos) {
            Usuario usuario = usuarioRepository.findByCorreo(correo.trim());
            if (usuario != null) {
                participantes.add(usuario);
            }
            // (Opcional: podrías notificar si algún correo no existe)
        }
        p.setParticipantes(participantes);
    }

    proyectoRepository.save(p);
    return "redirect:/proyectos";
}


}
