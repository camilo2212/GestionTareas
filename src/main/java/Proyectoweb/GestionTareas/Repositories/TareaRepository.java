package Proyectoweb.GestionTareas.Repositories;

import Proyectoweb.GestionTareas.models.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    // Buscar por estado
    List<Tarea> findByEstado(String estado);

    // Buscar por prioridad
    List<Tarea> findByPrioridad(String prioridad);

    // Buscar por responsable (id del usuario)
    List<Tarea> findByResponsableId(int usuarioId);

    // Buscar por proyecto (id)
    List<Tarea> findByProyectoId(int proyectoId);
}
