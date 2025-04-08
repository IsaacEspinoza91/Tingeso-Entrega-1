package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.ReporteIngresosVueltasDTO;
import com.kartingRM.AppKartingRM.services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReservaService reservaService;

    // Obtiene ingresos por mes segun plan. Utiliza la clase DTO de ReporteIngresosVueltas para estructurar los datos
    @GetMapping("/ingresos-por-vueltas")
    public ResponseEntity<List<ReporteIngresosVueltasDTO>> getReporteIngresosVueltas(@RequestParam int mes_inicio,
                                                                                     @RequestParam int anio_inicio,
                                                                                     @RequestParam int mes_fin,
                                                                                     @RequestParam int anio_fin) {

        List<ReporteIngresosVueltasDTO> reporte = reservaService.generarReporteIngresosVueltas(mes_inicio, anio_inicio, mes_fin, anio_fin);
        return ResponseEntity.ok(reporte);
    }
}
