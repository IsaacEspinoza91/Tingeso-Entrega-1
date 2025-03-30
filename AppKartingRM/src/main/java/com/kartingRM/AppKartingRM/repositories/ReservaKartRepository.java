package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.ReservaKartEntity;
import com.kartingRM.AppKartingRM.entities.ReservaKartId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaKartRepository extends JpaRepository<ReservaKartEntity, ReservaKartId> {
}
