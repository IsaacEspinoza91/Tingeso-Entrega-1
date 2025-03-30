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
@IdClass(ReservaKartId.class)
public class ReservaKartEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private ReservaEntity reserva;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_kart")
    private KartEntity kart;

}
