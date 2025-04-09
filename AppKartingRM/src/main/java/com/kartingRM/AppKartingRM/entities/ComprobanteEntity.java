package com.kartingRM.AppKartingRM.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private ReservaEntity reserva;

    @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference("comprobante-detalles") // Serializa esta relación
    private List<DetalleComprobanteEntity> detalles = new ArrayList<>();

}
