package Proyectoweb.GestionTareas.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    // Puedes usar un enum si prefieres: private Rol rol;
    @Column(nullable = false)
    private String rol; // ADMINISTRADOR o MIEMBRO

    // Asegúrate que el nombre coincida con el atributo en Tarea (por ejemplo, "responsable").
    @OneToMany(mappedBy = "responsable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarea> tareasAsignadas;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacion> notificaciones;
}
