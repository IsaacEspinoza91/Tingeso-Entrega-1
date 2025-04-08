package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.ComprobanteEntity;
import com.kartingRM.AppKartingRM.services.ComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comprobantes")
public class ComprobanteController {

    @Autowired
    private ComprobanteService comprobanteService;

    @GetMapping("/")
    public ResponseEntity<List<ComprobanteEntity>> getComprobantes() {
        List<ComprobanteEntity> comprobantes = comprobanteService.getComprobantes();
        return ResponseEntity.ok(comprobantes);
    }

    @GetMapping("/{id_comprobante}")
    public ResponseEntity<ComprobanteEntity> getComprobanteById(@PathVariable Long id_comprobante) {
        ComprobanteEntity comprobante = comprobanteService.getComprobanteById(id_comprobante);
        return ResponseEntity.ok(comprobante);
    }

    @PostMapping
    public ResponseEntity<ComprobanteEntity> createComprobante(@RequestBody ComprobanteEntity comprobante, @RequestParam Long id_reserva) {
        ComprobanteEntity comprobanteNuevo = comprobanteService.createComprobante(comprobante, id_reserva);
        return ResponseEntity.ok(comprobanteNuevo);
    }

    @PutMapping("/{id_comprobante}")
    public ResponseEntity<ComprobanteEntity> updateComprobante(@PathVariable Long id_comprobante, @RequestBody ComprobanteEntity comprobante) {
        ComprobanteEntity comprobanteActualizado = comprobanteService.updateComprobante(id_comprobante, comprobante);
        return ResponseEntity.ok(comprobanteActualizado);
    }

    @PutMapping("/{id_comprobante}/reserva")
    public ResponseEntity<ComprobanteEntity> updateReservaDeComprobante(@PathVariable Long id_comprobante, @RequestParam Long id_reserva){
        ComprobanteEntity comprobanteActualizado = comprobanteService.updateReservaDeComprobante(id_comprobante, id_reserva);
        return ResponseEntity.ok(comprobanteActualizado);
    }

    @DeleteMapping("/{id_comprobante}")
    public ResponseEntity<Boolean> deleteComprobante(@PathVariable Long id_comprobante) throws Exception{
        comprobanteService.deleteComprobante(id_comprobante);
        return ResponseEntity.noContent().build();
    }
}
