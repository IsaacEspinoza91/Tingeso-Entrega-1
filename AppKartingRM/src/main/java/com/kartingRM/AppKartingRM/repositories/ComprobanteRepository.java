package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.ComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprobanteRepository extends JpaRepository<ComprobanteEntity, Long> {
}
