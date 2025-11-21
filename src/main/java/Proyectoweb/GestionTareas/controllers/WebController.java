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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    // --- HOME ---
    @GetMapping("/")
    public String home() {
        return "index"; // index.html
    }

    // --- PROYECTOS ---
    @GetMapping("/proyectos")
    public String vistaProyectos(Model model) {
        model.addAttribute("proyectos", proyectoRepository.findAll());
        return "proyectos";
    }

    @PostMapping("/proyectos")
    public String crearProyecto(@RequestParam String nombre, @RequestParam String descripcion) {
        Proyecto p = new Proyecto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setFechaCreacion(new Date());
        proyectoRepository.save(p);
        return "redirect:/proyectos";
    }

    @PostMapping("/proyectos/{id}/eliminar")
    public String eliminarProyecto(@PathVariable Integer id) {
        proyectoRepository.deleteById(id);
        return "redirect:/proyectos";
    }

    // --- TAREAS ---
    @GetMapping("/tareas")
    public String vistaTareas(Model model,
                              @RequestParam(required = false) String query,
                              @RequestParam(required = false) String prioridad,
                              @RequestParam(required = false) String estado) {
        List<Tarea> tareas = tareaRepository.findAll();

        if (query != null && !query.isBlank()) {
            tareas = tareas.stream()
                .filter(t -> t.getTitulo().toLowerCase().contains(query.toLowerCase())
                          || t.getDescripcion().toLowerCase().contains(query.toLowerCase()))
                .toList();
        }
        if (prioridad != null && !prioridad.isBlank()) {
            tareas = tareas.stream()
                .filter(t -> prioridad.equals(t.getPrioridad()))
                .toList();
        }
        if (estado != null && !estado.isBlank()) {
            tareas = tareas.stream()
                .filter(t -> estado.equals(t.getEstado()))
                .toList();
        }

        List<Proyecto> proyectos = proyectoRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Dashboard con el subconjunto filtrado
        long pendientes = tareas.stream().filter(t -> "Pendiente".equals(t.getEstado())).count();
        long enProgreso = tareas.stream().filter(t -> "En progreso".equals(t.getEstado())).count();
        long completadas = tareas.stream().filter(t -> "Completada".equals(t.getEstado())).count();
        long prioridadAlta = tareas.stream().filter(t -> "Alta".equals(t.getPrioridad())).count();
        long prioridadMedia = tareas.stream().filter(t -> "Media".equals(t.getPrioridad())).count();
        long prioridadBaja = tareas.stream().filter(t -> "Baja".equals(t.getPrioridad())).count();

        model.addAttribute("tareas", tareas);
        model.addAttribute("usuarios", usuarios);

        // Indicadores para el dashboard interno de tareas
        model.addAttribute("tareasTotal", tareas.size());
        model.addAttribute("proyectosTotal", proyectos.size());
        model.addAttribute("tareasPendientes", pendientes);
        model.addAttribute("tareasEnProgreso", enProgreso);
        model.addAttribute("tareasCompletadas", completadas);
        model.addAttribute("prioridadAlta", prioridadAlta);
        model.addAttribute("prioridadMedia", prioridadMedia);
        model.addAttribute("prioridadBaja", prioridadBaja);

        return "tareas";
    }

    @PostMapping("/tareas")
    public String crearTarea(@RequestParam String titulo,
                             @RequestParam String descripcion,
                             @RequestParam String prioridad,
                             @RequestParam String estado,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaLimite,
                             @RequestParam Integer responsableId) {

        Usuario responsable = usuarioRepository.findById(responsableId).orElse(null);

        LocalDate localFechaLimite = fechaLimite.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime fechaLimiteLDT = localFechaLimite.atStartOfDay();
        LocalDateTime fechaCreacionLDT = LocalDateTime.now();

        Tarea t = new Tarea();
        t.setTitulo(titulo);
        t.setDescripcion(descripcion);
        t.setPrioridad(prioridad);
        t.setEstado(estado);
        t.setResponsable(responsable);
        t.setFechaLimite(fechaLimiteLDT);
        t.setFechaCreacion(fechaCreacionLDT);

        tareaRepository.save(t);
        return "redirect:/tareas";
    }

    @PostMapping("/tareas/{id}/editar")
    public String editarTarea(@PathVariable Integer id,
                              @RequestParam String titulo,
                              @RequestParam String descripcion,
                              @RequestParam String prioridad,
                              @RequestParam String estado,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaLimite,
                              @RequestParam Integer responsableId) {

        Tarea t = tareaRepository.findById(id).orElseThrow();
        t.setTitulo(titulo);
        t.setDescripcion(descripcion);
        t.setPrioridad(prioridad);
        t.setEstado(estado);
        Usuario responsable = usuarioRepository.findById(responsableId).orElse(null);
        t.setResponsable(responsable);

        LocalDate localFechaLimite = fechaLimite.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime fechaLimiteLDT = localFechaLimite.atStartOfDay();
        t.setFechaLimite(fechaLimiteLDT);

        tareaRepository.save(t);
        return "redirect:/tareas";
    }

    @PostMapping("/tareas/{id}/eliminar")
    public String eliminarTarea(@PathVariable Integer id) {
        tareaRepository.deleteById(id);
        return "redirect:/tareas";
    }

    // --- USUARIOS ---
    @GetMapping("/usuarios")
    public String vistaUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios";
    }

    @PostMapping("/usuarios")
    public String crearUsuario(@RequestParam String nombre,
                               @RequestParam String correo,
                               @RequestParam String contrasena,
                               @RequestParam String rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setContrasena(contrasena); // Sin codificar, solo para pruebas: usa BCrypt en producción.
        u.setRol(rol);
        usuarioRepository.save(u);
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuarios";
    }

    // --- NOTIFICACIONES ---
    @GetMapping("/notificaciones")
    public String vistaNotificaciones(Model model) {
        model.addAttribute("notificaciones", notificacionRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "notificaciones";
    }

    @PostMapping("/notificaciones")
    public String crearNotificacion(@RequestParam String mensaje,
                                    @RequestParam String tipo,
                                    @RequestParam Integer destinatarioId) {
        Notificacion n = new Notificacion();
        n.setMensaje(mensaje);
        n.setTipo(tipo);
        n.setLeida(false);
        n.setFecha(LocalDateTime.now());
        n.setDestinatario(usuarioRepository.findById(destinatarioId).orElse(null));
        notificacionRepository.save(n);
        return "redirect:/notificaciones";
    }

    @PostMapping("/notificaciones/{id}/eliminar")
    public String eliminarNotificacion(@PathVariable Integer id) {
        notificacionRepository.deleteById(id);
        return "redirect:/notificaciones";
    }
}
