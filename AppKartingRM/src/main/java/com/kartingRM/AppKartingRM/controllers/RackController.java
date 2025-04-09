package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.RackSemanalDTO;
import com.kartingRM.AppKartingRM.services.RackReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rackSemanal")
public class RackController {

    @Autowired
    private RackReservaService rackService;

    // Obtener reservas de la semana, comenzando por el dia lunes y terminando el domingo. Ademas mediante el parametro
    //   se puede obtener la semana actual, anterior, siguiente u otras.
    //  0 : semana actual
    //  1 : semana proxima
    // -1 : semena anterior
    @GetMapping("/")
    public ResponseEntity<RackSemanalDTO> getRackSemanal(@RequestParam(defaultValue = "0") int semana) {
        return ResponseEntity.ok(rackService.obtenerRackSemanal(semana));
    }
}