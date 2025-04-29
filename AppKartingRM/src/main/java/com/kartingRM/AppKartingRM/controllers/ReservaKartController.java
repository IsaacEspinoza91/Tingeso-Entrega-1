package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.ReservaKartEntity;
import com.kartingRM.AppKartingRM.services.ReservaKartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservaKarts")
@CrossOrigin("*")
public class ReservaKartController {

    @Autowired
    private ReservaKartService reservaKartService;


    // Obtener todas las relaciones reservaKart
    @GetMapping("/")
    public ResponseEntity<List<ReservaKartEntity>> getAllReservaKarts() {
        List<ReservaKartEntity> reservaKarts = reservaKartService.getAllReservaKarts();
        return ResponseEntity.ok(reservaKarts);
    }

    // Obtener karts de una reserva específica
    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<List<ReservaKartEntity>> getKartsByReserva(@PathVariable Long idReserva) {
        List<ReservaKartEntity> reservaKarts = reservaKartService.getKartsByReserva(idReserva);
        return ResponseEntity.ok(reservaKarts);
    }

    // Obtener todas las reservas de un kart segun id
    @GetMapping("/kart/{idKart}")
    public ResponseEntity<List<ReservaKartEntity>> getReservasByKart(@PathVariable Long idKart) {
        List<ReservaKartEntity> reservaKarts = reservaKartService.getReservasByKart(idKart);
        return ResponseEntity.ok(reservaKarts);
    }

    // Crear nueva relación reservaKart
    @PostMapping
    public ResponseEntity<ReservaKartEntity> createReservaKart(@RequestParam Long id_reserva, @RequestParam Long id_kart) {
        ReservaKartEntity nuevaRelacion = reservaKartService.createReservaKart(id_reserva, id_kart);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaRelacion);
    }

    // Eliminar relación reservaKart segun ambas ids
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteReservaKart(@RequestParam Long id_reserva, @RequestParam Long id_kart) {
        reservaKartService.deleteReservaKart(id_reserva, id_kart);
        return ResponseEntity.noContent().build();
    }

    // Eliminar todas las relaciones reservaKart segun la id de una reserva
    @DeleteMapping("/delete/reserva/{idReserva}")
    public ResponseEntity<Void> deleteAllByReserva(@PathVariable Long idReserva) {
        reservaKartService.deleteAllByReserva(idReserva);
        return ResponseEntity.noContent().build();
    }
}
