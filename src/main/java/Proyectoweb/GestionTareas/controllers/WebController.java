package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Notificacion;
import Proyectoweb.GestionTareas.models.Proyecto;
import Proyectoweb.GestionTareas.models.Tarea;
import Proyectoweb.GestionTareas.models.Usuario;
import Proyectoweb.GestionTareas.Repositories.NotificacionRepository;
import Proyectoweb.GestionTareas.Repositories.ProyectoRepository;
import Proyectoweb.GestionTareas.Repositories.TareaRepository;
import Proyectoweb.GestionTareas.Repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller
public class WebController {
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private TareaRepository tareaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private NotificacionRepository notificacionRepository;

    /** UTILIDAD PARA CREAR NOTIFICACIONES **/
    private void crearNotificacion(Usuario destinatario, String tipo, String mensaje) {
        Notificacion notif = new Notificacion();
        notif.setDestinatario(destinatario);
        notif.setTipo(tipo);
        notif.setMensaje(mensaje);
        notif.setLeida(false);
        notif.setFecha(LocalDateTime.now());
        notificacionRepository.save(notif);
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("activePage", "home");
        return "index";
    }

    @GetMapping("/proyectos")
    public String vistaProyectos(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        List<Proyecto> proyectos = proyectoRepository.findByParticipantes_Id(usuario.getId());
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("activePage", "proyectos");
        return "proyectos";
    }

    @PostMapping("/proyectos")
    public String crearProyecto(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam(required = false) String correosParticipantes,
            HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Proyecto p = new Proyecto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setFechaCreacion(new Date());
        p.setResponsable(usuario);

        List<Usuario> participantes = new ArrayList<>();
        participantes.add(usuario); // incluir al creador
        if (correosParticipantes != null && !correosParticipantes.trim().isEmpty()) {
            String[] correos = correosParticipantes.split(",");
            for (String correo : correos) {
                Usuario u = usuarioRepository.findByCorreo(correo.trim());
                if (u != null && !participantes.contains(u)) participantes.add(u);
            }
        }
        p.setParticipantes(participantes);
        proyectoRepository.save(p);

        // Notificaciones:
        crearNotificacion(usuario, "PROYECTO_NUEVO", "Has creado el proyecto: " + p.getNombre());
        for (Usuario u : participantes) {
            if (!u.getId().equals(usuario.getId())) {
                crearNotificacion(u, "PARTICIPANTE_AGREGADO", "Fuiste agregado al proyecto: " + p.getNombre());
            }
        }
        return "redirect:/proyectos";
    }

    @PostMapping("/proyectos/{id}/eliminar")
    public String eliminarProyecto(@PathVariable Integer id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Proyecto proyecto = proyectoRepository.findById(id).orElse(null);
        if (proyecto != null) {
            Integer usuarioEnSesionId = usuario.getId();
            boolean esParticipante = proyecto.getParticipantes().stream()
                    .anyMatch(u -> u.getId().equals(usuarioEnSesionId));
            if (esParticipante) {
                proyectoRepository.deleteById(id);
                // Notificación a todos los participantes
                for (Usuario u : proyecto.getParticipantes()) {
                    crearNotificacion(u, "PROYECTO_ELIMINADO", "El proyecto '" + proyecto.getNombre() + "' fue eliminado.");
                }
            }
        }
        return "redirect:/proyectos";
    }

    // ---------- TAREAS GLOBAL -----------
    @GetMapping("/tareas")
    public String vistaTareas(Model model,
                              @RequestParam(required = false) String query,
                              @RequestParam(required = false) String prioridad,
                              @RequestParam(required = false) String estado,
                              @RequestParam(required = false) Integer responsableId,
                              @RequestParam(required = false) Integer proyecto,
                              HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        List<Tarea> tareas;
        List<Proyecto> proyectosUsuario = proyectoRepository.findByParticipantes_Id(usuario.getId());
        List<Integer> ids = proyectosUsuario.stream().map(Proyecto::getId).toList();
        tareas = tareaRepository.findAll().stream()
                .filter(t -> t.getProyecto() != null && ids.contains(t.getProyecto().getId()))
                .toList();

        if (query != null && !query.isBlank()) {
            tareas = tareas.stream()
                    .filter(t -> t.getTitulo().toLowerCase().contains(query.toLowerCase())
                            || t.getDescripcion().toLowerCase().contains(query.toLowerCase()))
                    .toList();
        }
        if (prioridad != null && !prioridad.isBlank()) {
            tareas = tareas.stream().filter(t -> prioridad.equals(t.getPrioridad())).toList();
        }
        if (estado != null && !estado.isBlank()) {
            tareas = tareas.stream().filter(t -> estado.equals(t.getEstado())).toList();
        }
        if (responsableId != null) {
            tareas = tareas.stream()
                    .filter(t -> t.getResponsable() != null && responsableId.equals(t.getResponsable().getId()))
                    .toList();
        }

        List<Proyecto> proyectos = proyectosUsuario;
        List<Usuario> usuarios = new ArrayList<>();

        long pendientes = tareas.stream().filter(t -> "Pendiente".equalsIgnoreCase(t.getEstado())).count();
        long enProgreso = tareas.stream().filter(t -> "En progreso".equalsIgnoreCase(t.getEstado())).count();
        long completadas = tareas.stream().filter(t -> "Completada".equalsIgnoreCase(t.getEstado())).count();
        long prioridadAlta = tareas.stream().filter(t -> "Alta".equalsIgnoreCase(t.getPrioridad())).count();
        long prioridadMedia = tareas.stream().filter(t -> "Media".equalsIgnoreCase(t.getPrioridad())).count();
        long prioridadBaja = tareas.stream().filter(t -> "Baja".equalsIgnoreCase(t.getPrioridad())).count();

        model.addAttribute("proyectos", proyectos);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("tareas", tareas);
        model.addAttribute("tareasTotal", tareas.size());
        model.addAttribute("tareasPendientes", pendientes);
        model.addAttribute("tareasEnProgreso", enProgreso);
        model.addAttribute("tareasCompletadas", completadas);
        model.addAttribute("prioridadAlta", prioridadAlta);
        model.addAttribute("prioridadMedia", prioridadMedia);
        model.addAttribute("prioridadBaja", prioridadBaja);
        model.addAttribute("activePage", "tareas");
        return "tareas";
    }

    @PostMapping("/tareas")
    public String crearTarea(@RequestParam String titulo,
                             @RequestParam String descripcion,
                             @RequestParam String prioridad,
                             @RequestParam String estado,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaLimite,
                             @RequestParam Integer proyectoId,
                             HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElse(null);
        if (proyecto == null) return "redirect:/tareas";

        LocalDateTime fechaCreacion = LocalDateTime.now();
        LocalDateTime fechaLimiteLDT = fechaLimite.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Tarea t = new Tarea();
        t.setTitulo(titulo);
        t.setDescripcion(descripcion);
        t.setPrioridad(prioridad);
        t.setEstado(estado);
        t.setFechaLimite(fechaLimiteLDT);
        t.setFechaCreacion(fechaCreacion);
        t.setProyecto(proyecto);

        tareaRepository.save(t);

        // Notificar a todos los participantes del proyecto al crear tarea
        for (Usuario u : proyecto.getParticipantes()) {
            crearNotificacion(u, "TAREA_NUEVA", "Nueva tarea '" + t.getTitulo() + "' en el proyecto '" + proyecto.getNombre() + "'.");
        }
        return "redirect:/tareas";
    }

    @PostMapping("/tareas/{id}/editar")
    public String editarTarea(@PathVariable Integer id,
                              @RequestParam String titulo,
                              @RequestParam String descripcion,
                              @RequestParam String prioridad,
                              @RequestParam String estado,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaLimite,
                              @RequestParam Integer proyectoId,
                              HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElse(null);
        if (proyecto == null) return "redirect:/tareas";
        Tarea t = tareaRepository.findById(id).orElseThrow();

        t.setTitulo(titulo);
        t.setDescripcion(descripcion);
        t.setPrioridad(prioridad);
        t.setEstado(estado);
        t.setFechaLimite(fechaLimite.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        t.setProyecto(proyecto);

        tareaRepository.save(t);

        // Notificar edición a participantes (opcional)
        for (Usuario u : proyecto.getParticipantes()) {
            crearNotificacion(u, "TAREA_EDITADA", "La tarea '" + t.getTitulo() + "' fue editada en el proyecto '" + proyecto.getNombre() + "'.");
        }
        return "redirect:/tareas";
    }

    @PostMapping("/tareas/{id}/eliminar")
    public String eliminarTarea(@PathVariable Integer id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Tarea tarea = tareaRepository.findById(id).orElse(null);
        if (tarea != null && tarea.getProyecto() != null) {
            Proyecto proyecto = tarea.getProyecto();
            for (Usuario u : proyecto.getParticipantes()) {
                crearNotificacion(u, "TAREA_ELIMINADA", "La tarea '" + tarea.getTitulo() + "' fue eliminada de '" + proyecto.getNombre() + "'.");
            }
        }
        tareaRepository.deleteById(id);
        return "redirect:/tareas";
    }

    @GetMapping("/tareas/{id}/detalle")
    public String detalleTarea(@PathVariable Integer id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Tarea t = tareaRepository.findById(id).orElseThrow();
        model.addAttribute("tarea", t);
        model.addAttribute("activePage", "tareas");
        return "tarea-detalle";
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        List<Proyecto> proyectos = proyectoRepository.findByParticipantes_Id(usuario.getId());
        List<Integer> ids = proyectos.stream().map(Proyecto::getId).toList();
        List<Tarea> tareas = tareaRepository.findAll().stream()
                .filter(t -> t.getProyecto() != null && ids.contains(t.getProyecto().getId()))
                .toList();

        long tareasPendientes = tareas.stream().filter(t -> "Pendiente".equalsIgnoreCase(t.getEstado())).count();
        long tareasEnProgreso = tareas.stream().filter(t -> "En progreso".equalsIgnoreCase(t.getEstado())).count();
        long tareasCompletadas = tareas.stream().filter(t -> "Completada".equalsIgnoreCase(t.getEstado())).count();
        long prioridadAlta = tareas.stream().filter(t -> "Alta".equalsIgnoreCase(t.getPrioridad())).count();
        long prioridadMedia = tareas.stream().filter(t -> "Media".equalsIgnoreCase(t.getPrioridad())).count();
        long prioridadBaja = tareas.stream().filter(t -> "Baja".equalsIgnoreCase(t.getPrioridad())).count();

        model.addAttribute("tareasTotal", tareas.size());
        model.addAttribute("proyectosTotal", proyectos.size());
        model.addAttribute("tareasPendientes", tareasPendientes);
        model.addAttribute("tareasEnProgreso", tareasEnProgreso);
        model.addAttribute("tareasCompletadas", tareasCompletadas);
        model.addAttribute("prioridadAlta", prioridadAlta);
        model.addAttribute("prioridadMedia", prioridadMedia);
        model.addAttribute("prioridadBaja", prioridadBaja);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
