package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Notificacion;
import Proyectoweb.GestionTareas.Repositories.NotificacionRepository;
import Proyectoweb.GestionTareas.Repositories.UsuarioRepository;
import Proyectoweb.GestionTareas.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todas las notificaciones
    @GetMapping
    public List<Notificacion> getAllNotificaciones() {
        return notificacionRepository.findAll();
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> getNotificacionById(@PathVariable int id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + id));
        return ResponseEntity.ok(notificacion);
    }

    // Crear notificación
    @PostMapping
    public Notificacion createNotificacion(@RequestBody Map<String, Object> datos) {
        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje((String) datos.get("mensaje"));
        notificacion.setLeida(false);
        notificacion.setFecha(LocalDateTime.now());
        notificacion.setTipo((String) datos.getOrDefault("tipo", "INFO"));

        // Extrae el id de destinatario y lo busca
        Integer destinatarioId = (Integer) datos.get("destinatarioId");
        Usuario usuario = usuarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
        notificacion.setDestinatario(usuario);

        return notificacionRepository.save(notificacion);
    }

    // Actualizar notificación
    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> updateNotificacion(@PathVariable int id, @RequestBody Notificacion detalles) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + id));

        notificacion.setMensaje(detalles.getMensaje());
        notificacion.setLeida(detalles.isLeida());
        notificacion.setTipo(detalles.getTipo());
        // Puedes actualizar destinatario si quieres, pero cuidado con la lógica

        Notificacion actualizada = notificacionRepository.save(notificacion);
        return ResponseEntity.ok(actualizada);
    }

    // Eliminar notificación
    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteNotificacion(@PathVariable int id) {
        notificacionRepository.deleteById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("eliminado", Boolean.TRUE);
        return response;
    }
}
