package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteId;
import com.kartingRM.AppKartingRM.services.ComprobanteService;
import com.kartingRM.AppKartingRM.services.DetalleComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<DetalleComprobanteEntity>  getDetalleComprobanteById(@RequestParam Long id_detalle, @RequestParam Long id_comprobante) {
        DetalleComprobanteId idCompuestoDetalle = new DetalleComprobanteId(id_detalle, id_comprobante);
        DetalleComprobanteEntity detalleComprobante = detalleComprobanteService.getDetalleComprobanteById(idCompuestoDetalle);
        return ResponseEntity.ok(detalleComprobante);
    }

    // Crear detalle de comprobante
    @PostMapping
    public ResponseEntity<DetalleComprobanteEntity> createDetalleComprobante(@RequestBody DetalleComprobanteEntity detalleComprobante, @RequestParam Long id_comprobante) {
        DetalleComprobanteEntity detalleNuevo = detalleComprobanteService.createDetalleComprobante(detalleComprobante, id_comprobante);
        //return ResponseEntity.ok(detalleNuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleNuevo);
    }


    // Actualizar detalle existente
    @PutMapping("/update")
    public ResponseEntity<DetalleComprobanteEntity> updateDetalle(@RequestParam Long id_comprobante,
                                                                  @RequestParam Long id_detalle,
                                                                  @RequestBody DetalleComprobanteEntity detalle) {
        DetalleComprobanteId id = new DetalleComprobanteId(id_detalle, id_comprobante);
        DetalleComprobanteEntity detalleActualizado = detalleComprobanteService.updateDetalle(id, detalle);
        return ResponseEntity.ok(detalleActualizado);
    }


    // Eliminar detalle
    /*
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteDetalle(
            @RequestParam Long id_comprobante,
            @RequestParam Long id_detalle) throws Exception {
        DetalleComprobanteId id = new DetalleComprobanteId(id_detalle, id_comprobante);
        detalleComprobanteService.deleteDetalle(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar detalle
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteDetalle(
            @RequestParam Long id_comprobante,
            @RequestParam Long id_detalle) {
        DetalleComprobanteId id = new DetalleComprobanteId(id_detalle, id_comprobante);
        detalleComprobanteService.deleteDetalle(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id_detalle}/{id_comprobante}")
    public ResponseEntity<Boolean> deleteDetalleComprobante(@PathVariable Long id_detalle, @PathVariable Long id_comprobante) throws Exception {

        DetalleComprobanteId idCompuestoDetalle = new DetalleComprobanteId(id_detalle, id_comprobante);
        DetalleComprobanteEntity detalleComprobante = detalleComprobanteService.getDetalleComprobanteById(idCompuestoDetalle);

        detalleComprobanteService.deleteDetalleComprobante(detalleComprobante.getIdDetalle());
        //detalleComprobanteService.deleteDetalleComprobante(id_detalle,id_comprobante);
        return ResponseEntity.noContent().build();
    }*/

}
