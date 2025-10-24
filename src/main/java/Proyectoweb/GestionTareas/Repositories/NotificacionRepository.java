package Proyectoweb.GestionTareas.Repositories;

import Proyectoweb.GestionTareas.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    // Buscar notificaciones por usuario
    List<Notificacion> findByUsuario_Id(int usuarioId);

    // Buscar solo las no leídas
    List<Notificacion> findByUsuario_IdAndLeidaFalse(int usuarioId);
}
