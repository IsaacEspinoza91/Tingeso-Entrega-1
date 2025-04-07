package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/")
    public ResponseEntity<List<ReservaEntity>> getReservas(){
        List<ReservaEntity> reservas = reservaService.getReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/por-cliente/{id_cliente}")
    public List<ReservaEntity> getReservasPorCliente(@PathVariable Long id_cliente) {
        return reservaService.getReservasByClienteId(id_cliente);
    }

    @GetMapping("/{id_reserva}")
    public ResponseEntity<ReservaEntity> getReservaById(@PathVariable Long id_reserva){
        ReservaEntity reserva = reservaService.getReservaById(id_reserva);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping
    public ResponseEntity<ReservaEntity> createReserva(@RequestBody ReservaEntity reserva, @RequestParam Long id_cliente, @RequestParam Long id_plan){
        ReservaEntity reservaNueva = reservaService.createReserva(reserva, id_cliente, id_plan);
        return ResponseEntity.ok(reservaNueva);
    }

    @PutMapping("/{id_reserva}")
    public ResponseEntity<ReservaEntity> updateReserva(@PathVariable Long id_reserva, @RequestBody ReservaEntity reserva){
        ReservaEntity reservaActualizada = reservaService.updateReserva(id_reserva, reserva);
        return ResponseEntity.ok(reservaActualizada);
    }

    @PutMapping("/{id_reserva}/cliente")
    public ResponseEntity<ReservaEntity> updateClienteDeReserva(@PathVariable Long id_reserva, @RequestParam Long id_cliente){
        ReservaEntity reservaActualizada = reservaService.updateClienteDeReserva(id_reserva, id_cliente);
        return ResponseEntity.ok(reservaActualizada);
    }

    @PutMapping("/{id_reserva}/plan")
    public ResponseEntity<ReservaEntity> updatePlanDeReserva(@PathVariable Long id_reserva, @RequestParam Long id_plan){
        ReservaEntity reservaActualizada = reservaService.updatePlanDeReserva(id_reserva, id_plan);
        return ResponseEntity.ok(reservaActualizada);
    }

    @DeleteMapping("/{id_reserva}")
    public ResponseEntity<Boolean> deleteReservaById(@PathVariable Long id_reserva) throws Exception{
        reservaService.deleteReserva(id_reserva);
        return ResponseEntity.noContent().build();
    }
}
