package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.ComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import com.kartingRM.AppKartingRM.services.ComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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



    @PostMapping("/segun-reserva/{id_reserva}")
    public ComprobanteEntity crearComprobanteDesdeReserva(@PathVariable Long id_reserva, @RequestParam Boolean feriado, @RequestParam Double descuento_extra) {
        return comprobanteService.crearComprobanteDesdeReserva(id_reserva, feriado, descuento_extra);
    }




    // Operaciones de DetalleComprobante

    // Obtiene todos los detalles
    @GetMapping("/detalles/")
    public ResponseEntity<List<DetalleComprobanteEntity>> getDetalleComprobantes() {
        List<DetalleComprobanteEntity> detallesComprobantes = comprobanteService.getDetalleComprobantes();
        return ResponseEntity.ok(detallesComprobantes);
    }

    // Obtener todos los detalles de un comprobante
    @GetMapping("/detalles/comprobante/{id_comprobante}")
    public ResponseEntity<List<DetalleComprobanteEntity>> getDetallesByComprobante(@PathVariable Long id_comprobante) {
        List<DetalleComprobanteEntity> AllDetallesDeComprobantes = comprobanteService.getDetallesByComprobante(id_comprobante);
        return ResponseEntity.ok(AllDetallesDeComprobantes);
    }

    // Obtener detalle en especifico segun id
    @GetMapping("/detalles/{id_detalle}")
    public ResponseEntity<DetalleComprobanteEntity> getReservaById(@PathVariable Long id_detalle){
        DetalleComprobanteEntity detalleComprobante = comprobanteService.getDetalleComprobanteById(id_detalle);
        return ResponseEntity.ok(detalleComprobante);
    }

    // Obtener todos los detalles de un un cliente segun su id
    @GetMapping("/detalles/cliente/{id_cliente}")
    public ResponseEntity<List<DetalleComprobanteEntity>> getDetalleComprobantesByClienteId(@PathVariable Long id_cliente) {
        List<DetalleComprobanteEntity> detalles = comprobanteService.getDetalleComprobantesByClienteId(id_cliente);
        return ResponseEntity.ok(detalles);
    }

    // Obtener detalle segun id de cliente e id de comprobante
    @GetMapping("/detalles")
    public ResponseEntity<DetalleComprobanteEntity> getDetalleComprobanteByClienteIdAndComprobanteId(@RequestParam Long id_cliente,
                                                                                                     @RequestParam Long id_comprobante) {
        DetalleComprobanteEntity detalle = comprobanteService.getDetalleComprobanteByClienteIdAndComprobanteId(id_cliente, id_comprobante);
        return ResponseEntity.ok(detalle);
    }


    // Crear detalle de comprobante, indicando id de comprobante e id de cliente
    @PostMapping("/detalles")
    public ResponseEntity<DetalleComprobanteEntity> createDetalleComprobante(@RequestBody DetalleComprobanteEntity detalleComprobante,
                                                                             @RequestParam Long id_comprobante,
                                                                             @RequestParam Long id_cliente) {
        DetalleComprobanteEntity detalleNuevo = comprobanteService.createDetalleComprobante(
                detalleComprobante, id_comprobante, id_cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleNuevo);
    }


    // Actualizar detalle existente
    @PutMapping("/detalles/update/{id_detalle}")
    public ResponseEntity<DetalleComprobanteEntity> updateDetalle(@PathVariable Long id_detalle, @RequestBody DetalleComprobanteEntity detalle) {
        DetalleComprobanteEntity detalleActualizado = comprobanteService.updateDetalle(id_detalle, detalle);
        return ResponseEntity.ok(detalleActualizado);
    }


    // Actualizar cliente de un detalle
    @PutMapping("/detalles/update/{id_detalle}/cliente")
    public ResponseEntity<DetalleComprobanteEntity> updateClienteDeDetalle(@PathVariable Long id_detalle, @RequestParam Long id_cliente) {
        DetalleComprobanteEntity detalleActualizado = comprobanteService.updateClienteDeDetalle(id_detalle, id_cliente);
        return ResponseEntity.ok(detalleActualizado);
    }


    // Eliminar detalle
    @DeleteMapping("/detalles/delete/{id_detalle}")
    public ResponseEntity<Boolean> deleteDetalleComprobanteById(@PathVariable Long id_detalle) throws Exception{
        comprobanteService.deleteDetalleComprobante(id_detalle);
        return ResponseEntity.noContent().build();
    }
}
