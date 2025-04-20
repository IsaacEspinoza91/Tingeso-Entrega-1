package com.kartingRM.AppKartingRM.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String rut;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;

    @Temporal(TemporalType.DATE)
    private LocalDate fechaNacimiento;



    @JsonIgnore     // Evitar mostrarlo en el json de las peticiones, evita recursion infinita
    @OneToMany(mappedBy = "reservante")
    private List<ReservaEntity> reservasComoArrendatario = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "integrantes")
    private List<ReservaEntity> reservasComoIntegrante = new ArrayList<>();
}
