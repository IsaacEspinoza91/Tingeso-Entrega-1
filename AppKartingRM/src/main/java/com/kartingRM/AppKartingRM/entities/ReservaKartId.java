package com.kartingRM.AppKartingRM.entities;

import java.io.Serializable;
import java.util.Objects;

public class ReservaKartId implements Serializable {

    private Long reserva;
    private Long kart;


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ReservaKartId reservaKartId = (ReservaKartId) o;
        return Objects.equals(reserva, reservaKartId.reserva) &&
                Objects.equals(kart, reservaKartId.kart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reserva, kart);
    }
}
