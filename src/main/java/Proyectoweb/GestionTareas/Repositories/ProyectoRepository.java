package Proyectoweb.GestionTareas.Repositories;

import Proyectoweb.GestionTareas.models.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    // Buscar proyectos por responsable
    List<Proyecto> findByResponsable_Id(int usuarioId);
    // Buscar proyectos donde el usuario es participante
    List<Proyecto> findByParticipantes_Id(Integer usuarioId);

    
}
