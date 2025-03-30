package com.kartingRM.AppKartingRM.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @Temporal(TemporalType.DATE)
    private Date fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private int totalPersonas;

    @ManyToOne  // El Many se refiere a la entidad actual a one de la de abajo (plan)
    @JoinColumn(name = "id_plan")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "rut_reservante")
    private Cliente reservante;
}
