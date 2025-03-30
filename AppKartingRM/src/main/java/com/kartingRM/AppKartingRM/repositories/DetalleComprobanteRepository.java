package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleComprobanteRepository extends JpaRepository<DetalleComprobanteEntity, DetalleComprobanteId> {
}
