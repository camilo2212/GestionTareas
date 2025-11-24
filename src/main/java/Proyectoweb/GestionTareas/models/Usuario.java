package Proyectoweb.GestionTareas.models;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(nullable = false)
    private String rol; // ADMINISTRADOR o MIEMBRO

    // Relación con Tarea, evita ciclo con @JsonIgnore y @ToString.Exclude
    @OneToMany(mappedBy = "responsable", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // <-- ¡esto es crítico!
    @ToString.Exclude
    private List<Tarea> tareasAsignadas;

    // Relación con Notificacion, igual
    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Notificacion> notificaciones;
}

