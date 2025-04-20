package com.kartingRM.AppKartingRM.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_comprobante")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleComprobanteEntity {

    @EmbeddedId
    private DetalleComprobanteId idDetalle;

    private int tarifa;
    private double descuentoGrupo;
    private double descuentoEspecial;      // Descuento cliente frecuente o cumpleanios
    private double descuentoExtra = 0;         // Descuento manual extra, default 0
    private double montoFinal;             // Despues de aplicar tarifas y descuentos
    private double montoIva;
    private double montoTotal;             // Total incluyendo iva

    @ManyToOne
    @MapsId("idComprobante")
    @JoinColumn(name = "id_comprobante")
    @JsonBackReference("comprobante-detalles") // No serializa esta relación
    private ComprobanteEntity comprobante;

    // Relación UNIDIRECCIONAL con Cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntity cliente;
}
