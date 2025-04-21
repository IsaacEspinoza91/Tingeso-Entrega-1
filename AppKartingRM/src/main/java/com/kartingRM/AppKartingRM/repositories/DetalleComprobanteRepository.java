package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleComprobanteRepository extends JpaRepository<DetalleComprobanteEntity, Long> {

    List<DetalleComprobanteEntity> findByComprobanteIdComprobante(Long idComprobante);

    DetalleComprobanteEntity findByClienteIdAndComprobanteIdComprobante(Long clienteId, Long comprobanteId);


    // Buscar todos los detallesComprobante por id de cliente
    @Query("SELECT d " +
            "FROM DetalleComprobanteEntity d " +
            "WHERE d.cliente.id = :clienteId")
    List<DetalleComprobanteEntity> findByClienteId(@Param("clienteId") Long clienteId);


    // Obtener la cantidad de veces que un cliente utiliza el karting en un mes especifico
    @Query("SELECT COUNT(d) FROM DetalleComprobanteEntity d " +
            "WHERE d.cliente.id = :clienteId " +
            "AND YEAR(d.comprobante.reserva.fecha) = :anio " +
            "AND MONTH(d.comprobante.reserva.fecha) = :mes")
    int contarVisitasByClienteYMes(@Param("clienteId") Long clienteId, @Param("anio") int anio, @Param("mes") int mes);
}
