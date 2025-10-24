package Proyectoweb.GestionTareas.Repositories;

import Proyectoweb.GestionTareas.models.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArchivoRepository extends JpaRepository<Archivo, Integer> {

    // Buscar archivos de una tarea específica
    List<Archivo> findByTarea_Id(int tareaId);
}
