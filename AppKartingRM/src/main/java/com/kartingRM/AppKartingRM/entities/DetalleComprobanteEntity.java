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

    private String nombre;
    private int tarifa;
    private int descuentoGrupo;
    private int descuentoEspecial;
    private int montoTotal;
    private int montoIva;
    private int montoFinal;

    @ManyToOne
    @MapsId("idComprobante")
    @JoinColumn(name = "id_comprobante")
    @JsonBackReference("comprobante-detalles") // No serializa esta relación
    private ComprobanteEntity comprobante;
}
