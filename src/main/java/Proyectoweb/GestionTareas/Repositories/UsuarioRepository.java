package Proyectoweb.GestionTareas.Repositories;

import Proyectoweb.GestionTareas.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Buscar usuario por correo (útil para login o validación)
    Usuario findByCorreo(String correo);
}
