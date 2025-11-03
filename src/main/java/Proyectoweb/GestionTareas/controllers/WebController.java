package Proyectoweb.GestionTareas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/proyectos")
    public String vistaProyectos() {
        return "proyectos"; // este archivo lo crearemos
    }

    @GetMapping("/usuarios")
    public String vistaUsuarios() {
        return "usuarios";
    }

    @GetMapping("/notificaciones")
    public String vistaNotificaciones() {
        return "notificaciones";
    }
}
