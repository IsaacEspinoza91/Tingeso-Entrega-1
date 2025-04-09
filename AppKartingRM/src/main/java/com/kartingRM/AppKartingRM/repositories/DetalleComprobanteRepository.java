package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleComprobanteRepository extends JpaRepository<DetalleComprobanteEntity, DetalleComprobanteId> {

    List<DetalleComprobanteEntity> findByComprobanteIdComprobante(Long idComprobante);

    DetalleComprobanteEntity findByClienteIdAndComprobanteIdComprobante(Long clienteId, Long comprobanteId);

    @Query("SELECT MAX(d.idDetalle.idDetalle) " +
            "FROM DetalleComprobanteEntity d " +
            "WHERE d.idDetalle.idComprobante = :idComprobante")
    Long findMaxIdDetalleByIdComprobante(@Param("idComprobante") Long idComprobante);


    // Buscar todos los detallesComprobante por id de cliente
    @Query("SELECT d " +
            "FROM DetalleComprobanteEntity d " +
            "WHERE d.cliente.id = :clienteId")
    List<DetalleComprobanteEntity> findByClienteId(@Param("clienteId") Long clienteId);
}
