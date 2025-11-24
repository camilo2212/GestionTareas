package Proyectoweb.GestionTareas.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    @Column(nullable = false)
    private String estado; // Pendiente, En progreso, Completada

    private String prioridad;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLimite;

    // Relación con Usuario (responsable)
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore  // IMPORTANTE si tu app ofrece serialización/rest
    private Usuario responsable;

    // Relación con Proyecto
    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore // idem arriba, por si acaso
    private Proyecto proyecto;
}
