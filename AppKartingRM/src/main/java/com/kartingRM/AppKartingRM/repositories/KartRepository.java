package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.KartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KartRepository extends JpaRepository<KartEntity, Long> {
}
