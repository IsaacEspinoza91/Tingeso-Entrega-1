package com.kartingRM.AppKartingRM.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteIngresosPersonasDTO {

    private String rangoPersonas;  // "1-2 personas", "3-5 personas" ...
    private Map<String, Double> ingresosPorMes;  // Llave: "mes-anio", Valor: monto
    private Double total;
    private boolean esTotalGeneral = false;
}