package Proyectoweb.GestionTareas.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String mensaje;

    private boolean leida = false;

    @Column(nullable = false)
    private String tipo; // "INFO", "ALERTA", etc. Opcional pero recomendado

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private Usuario destinatario;
}
