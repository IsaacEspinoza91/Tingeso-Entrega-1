package com.kartingRM.AppKartingRM.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

// Clase del tipo DTO (Data Tranfer Object), transfiere datos entre componentes de forma estructurada
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteIngresosVueltasDTO {
    private String descripcionPlan;
    private Map<String, Double> ingresosPorMes; // Llave: "mes-anio", Valor: monto
    private Double total;
    private boolean esTotalGeneral = false; // Identifica la fila final de los totales de ingresos
}
