package com.kartingRM.AppKartingRM.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comprobante")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComprobante;
    private boolean pagado;
    private int total;

    @OneToOne
    @JoinColumn(name = "id_reserva")
    private ReservaEntity reservaEntity;

}
