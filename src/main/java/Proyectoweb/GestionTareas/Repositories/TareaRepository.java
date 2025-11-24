package Proyectoweb.GestionTareas.Repositories;

import Proyectoweb.GestionTareas.models.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findByEstado(String estado);

    List<Tarea> findByPrioridad(String prioridad);

    List<Tarea> findByResponsableId(int usuarioId);

    List<Tarea> findByProyectoId(int proyectoId);
}
