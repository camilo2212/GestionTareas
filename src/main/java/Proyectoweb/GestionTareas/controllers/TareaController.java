package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Tarea;
import Proyectoweb.GestionTareas.Repositories.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/tareas")
public class TareaController {

    @Autowired
    private TareaRepository tareaRepository;

    // ✅ Obtener todas las tareas
    @GetMapping
    public List<Tarea> getAllTareas() {
        return tareaRepository.findAll();
    }

    // ✅ Obtener tarea por ID
    @GetMapping("/{id}")
    public ResponseEntity<Tarea> getTareaById(@PathVariable int id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
        return ResponseEntity.ok(tarea);
    }

    // ✅ Crear nueva tarea
    @PostMapping
    public Tarea createTarea(@RequestBody Tarea tarea) {
        tarea.setFechaCreacion(java.time.LocalDateTime.now());
        return tareaRepository.save(tarea);
    }

    // ✅ Actualizar tarea
    @PutMapping("/{id}")
    public ResponseEntity<Tarea> updateTarea(@PathVariable int id, @RequestBody Tarea detalles) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));

        tarea.setTitulo(detalles.getTitulo());
        tarea.setDescripcion(detalles.getDescripcion());
        tarea.setPrioridad(detalles.getPrioridad());
        tarea.setEstado(detalles.getEstado());
        tarea.setFechaLimite(detalles.getFechaLimite());

        Tarea actualizada = tareaRepository.save(tarea);
        return ResponseEntity.ok(actualizada);
    }

    // ✅ Eliminar tarea
    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteTarea(@PathVariable int id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));

        tareaRepository.delete(tarea);
        Map<String, Boolean> response = new HashMap<>();
        response.put("eliminado", Boolean.TRUE);
        return response;
    }

    // ✅ Filtrar por estado
    @GetMapping("/estado/{estado}")
    public List<Tarea> getTareasByEstado(@PathVariable String estado) {
        return tareaRepository.findByEstado(estado);
    }

    // ✅ Filtrar por prioridad
    @GetMapping("/prioridad/{prioridad}")
    public List<Tarea> getTareasByPrioridad(@PathVariable String prioridad) {
        return tareaRepository.findByPrioridad(prioridad);
    }

    // ✅ Filtrar por responsable
    @GetMapping("/responsable/{usuarioId}")
    public List<Tarea> getTareasByResponsable(@PathVariable int usuarioId) {
        return tareaRepository.findByResponsableId(usuarioId);
    }
}
