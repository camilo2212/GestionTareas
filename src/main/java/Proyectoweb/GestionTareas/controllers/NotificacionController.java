package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Notificacion;
import Proyectoweb.GestionTareas.models.Usuario;
import Proyectoweb.GestionTareas.Repositories.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @GetMapping("/notificaciones")
    public String verNotificaciones(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        List<Notificacion> notis = notificacionRepository.findByDestinatarioOrderByFechaDesc(usuario);
        model.addAttribute("notificaciones", notis);
        return "notificaciones";
    }

    @PostMapping("/notificaciones/{id}/leer")
    public String marcarNotificacionLeida(@PathVariable Integer id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Notificacion notif = notificacionRepository.findById(id).orElse(null);
        if (notif != null && notif.getDestinatario().getId().equals(usuario.getId())) {
            notif.setLeida(true);
            notificacionRepository.save(notif);
        }
        return "redirect:/notificaciones";
    }
    @PostMapping("/notificaciones/leertodas")
public String leerTodas(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";
    List<Notificacion> notifList = notificacionRepository.findByDestinatarioAndLeidaFalseOrderByFechaDesc(usuario);
    notifList.forEach(n -> n.setLeida(true));
    notificacionRepository.saveAll(notifList);
    return "redirect:/notificaciones";
}

}
