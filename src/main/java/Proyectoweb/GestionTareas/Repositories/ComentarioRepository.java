package Proyectoweb.GestionTareas.Repositories;

import Proyectoweb.GestionTareas.models.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    // Buscar comentarios de una tarea específica
    List<Comentario> findByTarea_Id(int tareaId);
}
