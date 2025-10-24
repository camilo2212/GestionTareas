package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Archivo;
import Proyectoweb.GestionTareas.Repositories.ArchivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/archivos")
public class ArchivoController {

    @Autowired
    private ArchivoRepository archivoRepository;

    @GetMapping
    public List<Archivo> getAllArchivos() {
        return archivoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Archivo> getArchivoById(@PathVariable int id) {
        Archivo archivo = archivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado con id: " + id));
        return ResponseEntity.ok(archivo);
    }

    @PostMapping
    public Archivo createArchivo(@RequestBody Archivo archivo) {
        return archivoRepository.save(archivo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Archivo> updateArchivo(@PathVariable int id, @RequestBody Archivo detalles) {
        Archivo archivo = archivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado con id: " + id));

        archivo.setNombre(detalles.getNombre());
        archivo.setRuta(detalles.getRuta());
        Archivo actualizado = archivoRepository.save(archivo);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteArchivo(@PathVariable int id) {
        Archivo archivo = archivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado con id: " + id));

        archivoRepository.delete(archivo);
        Map<String, Boolean> response = new HashMap<>();
        response.put("eliminado", Boolean.TRUE);
        return response;
    }
}
