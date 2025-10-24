package Proyectoweb.GestionTareas.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "archivos")
public class Archivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String ruta;

    @ManyToOne
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;
}
