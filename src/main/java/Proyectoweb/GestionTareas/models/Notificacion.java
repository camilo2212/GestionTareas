package Proyectoweb.GestionTareas.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String mensaje;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    private boolean leida;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario destinatario;
}
