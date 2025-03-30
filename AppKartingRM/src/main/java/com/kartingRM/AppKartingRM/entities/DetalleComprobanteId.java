package com.kartingRM.AppKartingRM.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

// Clase que permite que la DetalleComprobante tenga una llave primaria compuesta, esto para verificar que es entidad debil
@Embeddable
public class DetalleComprobanteId implements Serializable {
    private Long idDetalle;
    private Long comprobante;
}
