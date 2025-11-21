package Proyectoweb.GestionTareas.Repositories;

import Proyectoweb.GestionTareas.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByDestinatario_Id(int destinatarioId);
    List<Notificacion> findByDestinatario_IdAndLeidaFalse(int destinatarioId);
}
