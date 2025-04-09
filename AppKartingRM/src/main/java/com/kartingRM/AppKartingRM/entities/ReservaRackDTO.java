package com.kartingRM.AppKartingRM.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaRackDTO {

    private String codigoReserva;
    private String nombreReservante;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}