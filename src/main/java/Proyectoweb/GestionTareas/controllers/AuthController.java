package Proyectoweb.GestionTareas.controllers;

import Proyectoweb.GestionTareas.models.Usuario;
import jakarta.servlet.http.HttpSession;
import Proyectoweb.GestionTareas.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    // Procesar registro
    @PostMapping("/register")
    public String processRegister(@ModelAttribute Usuario usuario, Model model) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()) != null) {
            model.addAttribute("error", "El correo ya está registrado");
            return "register";
        }
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuario.setRol("MIEMBRO");
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }

    // Mostrar login
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String correo, @RequestParam String contrasena, Model model, HttpSession session) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario != null && passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            session.setAttribute("usuario", usuario);
            return "redirect:/proyectos"; 
        } else {
            model.addAttribute("error", "Correo o contraseña inválidos");
            return "login";
        }
    }
    
    @PostMapping("/logout")
public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
}

}
