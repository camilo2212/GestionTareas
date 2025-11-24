package Proyectoweb.GestionTareas.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion")
    private Date fechaCreacion = new Date();

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    @ToString.Exclude
    @JsonIgnore // por si hay ciclo responsable -> proyecto -> responsable ...
    private Usuario responsable;

    @OneToMany(mappedBy = "proyecto")
    @JsonIgnore // CRÍTICO: evita expandir a todas las tareas (que a su vez traen al proyecto, ciclando)
    @ToString.Exclude
    private List<Tarea> tareas;

    @ManyToMany
    @JoinTable(
        name = "proyecto_participantes",
        joinColumns = @JoinColumn(name = "proyecto_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @ToString.Exclude  // No necesitas ignorar para json ya que normalmente se lista por usuario simple
    private List<Usuario> participantes;
}
