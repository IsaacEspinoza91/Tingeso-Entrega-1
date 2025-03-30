package com.kartingRM.AppKartingRM.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

// Clase que permite que la DetalleComprobante tenga una llave primaria compuesta, esto para verificar que es entidad debil
@Embeddable
public class DetalleComprobanteId implements Serializable {
    private Long idDetalle;
    private Long comprobante;


    // Al usar llaves primarias compuestas, se deben sobre escibir estos metodos para evitar posibles errores (warnings)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DetalleComprobanteId detalleComprobanteId = (DetalleComprobanteId) o;
        return Objects.equals(idDetalle, detalleComprobanteId.idDetalle) &&
                Objects.equals(comprobante, detalleComprobanteId.comprobante);
    }


    @Override
    public int hashCode() {
        return Objects.hash(idDetalle, comprobante);
    }
}
