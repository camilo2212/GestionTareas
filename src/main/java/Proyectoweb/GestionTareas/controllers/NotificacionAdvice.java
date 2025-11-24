package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Notificacion;
import Proyectoweb.GestionTareas.models.Usuario;
import Proyectoweb.GestionTareas.Repositories.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@ControllerAdvice
public class NotificacionAdvice {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @ModelAttribute
    public void cargarNotificaciones(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            List<Notificacion> notis = notificacionRepository
                    .findByDestinatarioAndLeidaFalseOrderByFechaDesc(usuario);
            model.addAttribute("notificacionesNoLeidas", notis);
            model.addAttribute("numNotificacionesNoLeidas", notis.size());
        }
    }
}
