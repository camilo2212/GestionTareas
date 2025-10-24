package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Comentario;
import Proyectoweb.GestionTareas.Repositories.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @GetMapping
    public List<Comentario> getAllComentarios() {
        return comentarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> getComentarioById(@PathVariable int id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));
        return ResponseEntity.ok(comentario);
    }

    @PostMapping
    public Comentario createComentario(@RequestBody Comentario comentario) {
        comentario.setFechaComentario(new Date());
        return comentarioRepository.save(comentario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comentario> updateComentario(@PathVariable int id, @RequestBody Comentario detalles) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));

        comentario.setContenido(detalles.getContenido());
        Comentario actualizado = comentarioRepository.save(comentario);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteComentario(@PathVariable int id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));

        comentarioRepository.delete(comentario);
        Map<String, Boolean> response = new HashMap<>();
        response.put("eliminado", Boolean.TRUE);
        return response;
    }
}
