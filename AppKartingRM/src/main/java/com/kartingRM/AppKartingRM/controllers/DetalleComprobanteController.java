package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteId;
import com.kartingRM.AppKartingRM.services.DetalleComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/detalleComprobantes")
public class DetalleComprobanteController {

    @Autowired
    private DetalleComprobanteService detalleComprobanteService;

    @GetMapping("/")
    public ResponseEntity<List<DetalleComprobanteEntity>> getDetalleComprobantes() {
        List<DetalleComprobanteEntity> detallesComprobantes = detalleComprobanteService.getDetalleComprobantes();
        return ResponseEntity.ok(detallesComprobantes);
    }

    // Obtener todos los detalles de un comprobante
    @GetMapping("/comprobante/{id_comprobante}")
    public ResponseEntity<List<DetalleComprobanteEntity>> getDetallesByComprobante(@PathVariable Long id_comprobante) {
        List<DetalleComprobanteEntity> AllDetallesDeComprobantes = detalleComprobanteService.getDetallesByComprobante(id_comprobante);
        return ResponseEntity.ok(AllDetallesDeComprobantes);
    }

    // Obtener detalle en especifico
    @GetMapping("/detalle_especifico")
    public ResponseEntity<DetalleComprobanteEntity>  getDetalleComprobanteById(@RequestParam Long id_detalle,
                                                                               @RequestParam Long id_comprobante) {
        DetalleComprobanteId idCompuestoDetalle = new DetalleComprobanteId(id_detalle, id_comprobante);
        DetalleComprobanteEntity detalleComprobante = detalleComprobanteService.getDetalleComprobanteById(idCompuestoDetalle);
        return ResponseEntity.ok(detalleComprobante);
    }

    @GetMapping("/cliente/{id_cliente}")
    public ResponseEntity<List<DetalleComprobanteEntity>> getDetalleComprobantesByClienteId(@PathVariable Long id_cliente) {
        List<DetalleComprobanteEntity> detalles = detalleComprobanteService.getDetalleComprobantesByClienteId(id_cliente);
        return ResponseEntity.ok(detalles);
    }

    // Obtener detalle segun id de cliente e id de comprobante
    @GetMapping
    public ResponseEntity<DetalleComprobanteEntity> getDetalleComprobanteByClienteIdAndComprobanteId(@RequestParam Long id_cliente,
                                                                                                     @RequestParam Long id_comprobante) {
        DetalleComprobanteEntity detalle = detalleComprobanteService.getDetalleComprobanteByClienteIdAndComprobanteId(id_cliente, id_comprobante);
        return ResponseEntity.ok(detalle);
    }

    // Crear detalle de comprobante, indicando id de comprobante e id de cliente
    @PostMapping
    public ResponseEntity<DetalleComprobanteEntity> createDetalleComprobante(@RequestBody DetalleComprobanteEntity detalleComprobante,
                                                                             @RequestParam Long id_comprobante,
                                                                             @RequestParam Long id_cliente) {
        DetalleComprobanteEntity detalleNuevo = detalleComprobanteService.createDetalleComprobante(
                detalleComprobante, id_comprobante, id_cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleNuevo);
    }


    // Actualizar detalle existente
    @PutMapping("/update")
    public ResponseEntity<DetalleComprobanteEntity> updateDetalle(@RequestParam Long id_comprobante,
                                                                  @RequestParam Long id_detalle,
                                                                  @RequestParam Long id_cliente,
                                                                  @RequestBody DetalleComprobanteEntity detalle) {
        DetalleComprobanteId id = new DetalleComprobanteId(id_detalle, id_comprobante);
        DetalleComprobanteEntity detalleActualizado = detalleComprobanteService.updateDetalle(id, detalle, id_cliente);
        return ResponseEntity.ok(detalleActualizado);
    }


    // Eliminar detalle
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDetalleComprobante(@RequestParam Long id_detalle, @RequestParam Long id_comprobante) {
        try {
            detalleComprobanteService.deleteDetalleComprobante(id_detalle, id_comprobante);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje", "Error al eliminar detalle de comprobante",
                            "error", e.getMessage()
                    ));
        }
    }

}
