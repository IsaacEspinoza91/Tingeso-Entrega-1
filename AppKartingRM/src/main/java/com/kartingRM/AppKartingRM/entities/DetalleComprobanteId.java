package com.kartingRM.AppKartingRM.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

// Clase que permite que la DetalleComprobante tenga una llave primaria compuesta, esto para verificar que es entidad débil
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleComprobanteId implements Serializable {
    private Long idDetalle;
    private Long idComprobante;


    // Al usar llaves primarias compuestas, se deben sobrescribir estos métodos para evitar posibles errores (warnings)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DetalleComprobanteId detalleComprobanteId = (DetalleComprobanteId) o;
        return Objects.equals(idDetalle, detalleComprobanteId.idDetalle) &&
                Objects.equals(idComprobante, detalleComprobanteId.idComprobante);
    }


    @Override
    public int hashCode() {
        return Objects.hash(idDetalle, idComprobante);
    }
}
