package Proyectoweb.GestionTareas.models;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore // <-- solo si usas esta entidad en algún endpoint REST
    private Usuario destinatario;

    private String mensaje;
    private String tipo; // BIENVENIDA, PROYECTO_NUEVO, TAREA_NUEVA, VENCIMIENTO, ETC
    private LocalDateTime fecha = LocalDateTime.now();

    private boolean leida = false;
}
