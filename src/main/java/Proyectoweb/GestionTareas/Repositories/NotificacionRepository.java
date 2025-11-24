package Proyectoweb.GestionTareas.Repositories;

import Proyectoweb.GestionTareas.models.Notificacion;
import Proyectoweb.GestionTareas.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByDestinatarioAndLeidaFalseOrderByFechaDesc(Usuario destinatario);
    List<Notificacion> findByDestinatarioOrderByFechaDesc(Usuario destinatario);
}
