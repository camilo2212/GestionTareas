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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private ProyectoRepository proyectoRepository;
    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private NotificacionRepository notificacionRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    // PROYECTOS
    @GetMapping("/proyectos")
    public String vistaProyectos(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        List<Proyecto> proyectos = proyectoRepository.findByParticipantes_Id(usuario.getId());
        model.addAttribute("proyectos", proyectos);
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

        List<Usuario> participantes = new ArrayList<>();
        participantes.add(usuario);

        if (correosParticipantes != null && !correosParticipantes.trim().isEmpty()) {
            String[] correos = correosParticipantes.split(",");
            for (String correo : correos) {
                Usuario u = usuarioRepository.findByCorreo(correo.trim());
                if (u != null && !participantes.contains(u)) {
                    participantes.add(u);
                }
            }
        }

        p.setParticipantes(participantes);
        proyectoRepository.save(p);
        return "redirect:/proyectos";
    }

    @PostMapping("/proyectos/{id}/eliminar")
    public String eliminarProyecto(@PathVariable Integer id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Proyecto proyecto = proyectoRepository.findById(id).orElse(null);
        if (proyecto != null && proyecto.getParticipantes().contains(usuario)) {
            proyectoRepository.deleteById(id);
        }
        return "redirect:/proyectos";
    }

    // TAREAS
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
        if (proyecto != null) {
            Proyecto p = proyectoRepository.findById(proyecto).orElse(null);
            if (p == null || !p.getParticipantes().contains(usuario)) {
                tareas = List.of();
            } else {
                tareas = tareaRepository.findByProyectoId(proyecto);
            }
        } else {
            List<Proyecto> proyectosUsuario = proyectoRepository.findByParticipantes_Id(usuario.getId());
            List<Integer> ids = proyectosUsuario.stream().map(Proyecto::getId).toList();
            tareas = tareaRepository.findAll().stream()
                    .filter(t -> t.getProyecto() != null && ids.contains(t.getProyecto().getId()))
                    .toList();
        }

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

        List<Proyecto> proyectos = proyectoRepository.findByParticipantes_Id(usuario.getId());
        model.addAttribute("proyectos", proyectos);

        List<Usuario> usuarios;
        if (proyecto != null) {
            Proyecto p = proyectoRepository.findById(proyecto).orElse(null);
            usuarios = (p != null) ? p.getParticipantes() : new ArrayList<>();
        } else {
            usuarios = new ArrayList<>();
        }
        model.addAttribute("usuarios", usuarios);

        long pendientes = tareas.stream().filter(t -> "Pendiente".equalsIgnoreCase(t.getEstado())).count();
        long enProgreso = tareas.stream().filter(t -> "En progreso".equalsIgnoreCase(t.getEstado())).count();
        long completadas = tareas.stream().filter(t -> "Completada".equalsIgnoreCase(t.getEstado())).count();
        long prioridadAlta = tareas.stream().filter(t -> "Alta".equalsIgnoreCase(t.getPrioridad())).count();
        long prioridadMedia = tareas.stream().filter(t -> "Media".equalsIgnoreCase(t.getPrioridad())).count();
        long prioridadBaja = tareas.stream().filter(t -> "Baja".equalsIgnoreCase(t.getPrioridad())).count();

        model.addAttribute("tareas", tareas);
        model.addAttribute("tareasTotal", tareas.size());
        model.addAttribute("tareasPendientes", pendientes);
        model.addAttribute("tareasEnProgreso", enProgreso);
        model.addAttribute("tareasCompletadas", completadas);
        model.addAttribute("prioridadAlta", prioridadAlta);
        model.addAttribute("prioridadMedia", prioridadMedia);
        model.addAttribute("prioridadBaja", prioridadBaja);

        return "tareas";
    }

    // CREAR TAREA puede tener responsable nulo
    @PostMapping("/tareas")
    public String crearTarea(@RequestParam String titulo,
                             @RequestParam String descripcion,
                             @RequestParam String prioridad,
                             @RequestParam String estado,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaLimite,
                             @RequestParam(required = false) Integer responsableId,
                             @RequestParam Integer proyectoId,
                             HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElse(null);
        if (proyecto == null || !proyecto.getParticipantes().contains(usuario)) return "redirect:/tareas";

        Usuario responsable = null;
        if (responsableId != null) {
            Usuario posible = usuarioRepository.findById(responsableId).orElse(null);
            if (posible != null && proyecto.getParticipantes().contains(posible)) {
                responsable = posible;
            }
        }

        LocalDateTime fechaCreacion = LocalDateTime.now();
        LocalDateTime fechaLimiteLDT = fechaLimite.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Tarea t = new Tarea();
        t.setTitulo(titulo);
        t.setDescripcion(descripcion);
        t.setPrioridad(prioridad);
        t.setEstado(estado);
        t.setFechaLimite(fechaLimiteLDT);
        t.setFechaCreacion(fechaCreacion);
        t.setResponsable(responsable);
        t.setProyecto(proyecto);

        tareaRepository.save(t);
        return "redirect:/tareas?proyecto=" + proyectoId;
    }

    // EDITAR TAREA puede dejar responsable en null (sin responsable)
    @PostMapping("/tareas/{id}/editar")
    public String editarTarea(@PathVariable Integer id,
                              @RequestParam String titulo,
                              @RequestParam String descripcion,
                              @RequestParam String prioridad,
                              @RequestParam String estado,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaLimite,
                              @RequestParam(required = false) Integer responsableId,
                              @RequestParam Integer proyectoId,
                              HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElse(null);
        if (proyecto == null || !proyecto.getParticipantes().contains(usuario)) return "redirect:/tareas";

        Usuario responsable = null;
        if (responsableId != null) {
            Usuario posible = usuarioRepository.findById(responsableId).orElse(null);
            if (posible != null && proyecto.getParticipantes().contains(posible)) {
                responsable = posible;
            }
        }

        Tarea t = tareaRepository.findById(id).orElseThrow();

        t.setTitulo(titulo);
        t.setDescripcion(descripcion);
        t.setPrioridad(prioridad);
        t.setEstado(estado);
        t.setResponsable(responsable);

        LocalDateTime fechaLimiteLDT = fechaLimite.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        t.setFechaLimite(fechaLimiteLDT);
        t.setProyecto(proyecto);

        tareaRepository.save(t);
        return "redirect:/tareas?proyecto=" + proyectoId;
    }

    @PostMapping("/tareas/{id}/eliminar")
    public String eliminarTarea(@PathVariable Integer id, @RequestParam Integer proyectoId, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElse(null);
        if (proyecto == null || !proyecto.getParticipantes().contains(usuario)) return "redirect:/tareas";
        tareaRepository.deleteById(id);
        return "redirect:/tareas?proyecto=" + proyectoId;
    }

    // DETALLE DE TAREA
    @GetMapping("/tareas/{id}/detalle")
    public String detalleTarea(@PathVariable Integer id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Tarea t = tareaRepository.findById(id).orElseThrow();
        if (t.getProyecto() == null || !t.getProyecto().getParticipantes().contains(usuario)) return "redirect:/tareas";
        model.addAttribute("tarea", t);
        return "tarea-detalle";
    }

    // Otros métodos (notificaciones/usuarios) puedes agregarlos igual que antes si los necesitas
}
