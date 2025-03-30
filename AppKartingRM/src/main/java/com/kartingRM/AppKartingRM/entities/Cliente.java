package com.kartingRM.AppKartingRM.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @Column(unique = true, nullable = false)    //No se genera automaticamente la id (rut)
    private String rut;

    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;

    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

}
