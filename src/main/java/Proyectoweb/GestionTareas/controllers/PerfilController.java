package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Usuario;
import Proyectoweb.GestionTareas.models.Tarea;
import Proyectoweb.GestionTareas.models.Proyecto;
import Proyectoweb.GestionTareas.Repositories.ProyectoRepository;
import Proyectoweb.GestionTareas.Repositories.TareaRepository;
import Proyectoweb.GestionTareas.Repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class PerfilController {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public String verPerfil(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        
        // Proyectos donde participa
        List<Proyecto> proyectosParticipa = proyectoRepository.findByParticipantes_Id(usuario.getId());
        int proyectosCount = proyectosParticipa != null ? proyectosParticipa.size() : 0;

        // Tareas asignadas usando el ID del usuario
        List<Tarea> tareasAsignadas = tareaRepository.findByResponsableId(usuario.getId());
        int tareasAsignadasCount = tareasAsignadas != null ? tareasAsignadas.size() : 0;
        long tareasCompletadas = tareasAsignadas == null ? 0 :
                tareasAsignadas.stream().filter(
                        t -> t.getEstado() != null && t.getEstado().equalsIgnoreCase("Completada")
                ).count();

        model.addAttribute("usuario", usuario);
        model.addAttribute("proyectosParticipa", proyectosCount);
        model.addAttribute("tareasAsignadas", tareasAsignadasCount);
        model.addAttribute("tareasCompletadas", tareasCompletadas);

        return "perfil";
    }

@GetMapping("/perfil/editar")
public String editarPerfilForm(Model model, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";
    model.addAttribute("usuario", usuario);
    return "perfil-editar";
}

@PostMapping("/perfil/editar")
public String editarPerfil(
        @RequestParam String nombre,
        @RequestParam String correo,
        Model model,
        HttpSession session
) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";
    Usuario otro = usuarioRepository.findByCorreo(correo);
    if (otro != null && !otro.getId().equals(usuario.getId())) {
        model.addAttribute("usuario", usuario);
        model.addAttribute("error", "Ese correo ya está en uso por otro usuario.");
        return "perfil-editar";
    }
    usuario.setNombre(nombre);
    usuario.setCorreo(correo);
    usuarioRepository.save(usuario);
    // Refresca en sesión
    session.setAttribute("usuario", usuario);
    model.addAttribute("usuario", usuario);
    model.addAttribute("mensaje", "Perfil actualizado correctamente.");
    return "perfil-editar";
}

@Autowired
private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

@GetMapping("/perfil/cambiar-contrasena")
public String cambiarContrasenaForm(Model model, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";
    return "perfil-cambiar-contrasena";
}

@PostMapping("/perfil/cambiar-contrasena")
public String cambiarContrasena(
        @RequestParam String actual,
        @RequestParam String nueva,
        @RequestParam String confirmacion,
        Model model,
        HttpSession session
) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";

    if (!passwordEncoder.matches(actual, usuario.getContrasena())) {
        model.addAttribute("error", "La contraseña actual es incorrecta.");
        return "perfil-cambiar-contrasena";
    }
    if (!nueva.equals(confirmacion)) {
        model.addAttribute("error", "La nueva contraseña y la confirmación no coinciden.");
        return "perfil-cambiar-contrasena";
    }
    usuario.setContrasena(passwordEncoder.encode(nueva));
    usuarioRepository.save(usuario);

    session.setAttribute("usuario", usuario);
    model.addAttribute("mensaje", "Contraseña cambiada exitosamente.");
    return "perfil-cambiar-contrasena";
}


}
