package com.kartingRM.AppKartingRM.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RackSemanalDTO {

    private LocalDate fechaInicioSemana; // Fecha dia lunes
    private LocalDate fechaFinSemana;    // Fecha dia domingo
    private Map<String, List<ReservaRackDTO>> reservasPorDia; // Llave: "Lunes", Clave: ReservaRackDTO
}