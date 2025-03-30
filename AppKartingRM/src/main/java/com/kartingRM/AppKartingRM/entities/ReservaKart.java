package com.kartingRM.AppKartingRM.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reserva_kart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaKart {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_kart")
    private Kart kart;

}
