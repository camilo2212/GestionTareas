package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Notificacion;
import Proyectoweb.GestionTareas.Repositories.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @GetMapping
    public List<Notificacion> getAllNotificaciones() {
        return notificacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> getNotificacionById(@PathVariable int id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + id));
        return ResponseEntity.ok(notificacion);
    }

    @PostMapping
    public Notificacion createNotificacion(@RequestBody Notificacion notificacion) {
        notificacion.setFecha(new Date());
        notificacion.setLeida(false);
        return notificacionRepository.save(notificacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> updateNotificacion(@PathVariable int id, @RequestBody Notificacion detalles) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + id));

        notificacion.setMensaje(detalles.getMensaje());
        notificacion.setLeida(detalles.isLeida());

        Notificacion actualizada = notificacionRepository.save(notificacion);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteNotificacion(@PathVariable int id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + id));

        notificacionRepository.delete(notificacion);
        Map<String, Boolean> response = new HashMap<>();
        response.put("eliminado", Boolean.TRUE);
        return response;
    }
}
