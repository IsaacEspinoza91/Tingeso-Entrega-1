package com.kartingRM.AppKartingRM.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idkart;

    private String modelo;
    private String estado;

}
