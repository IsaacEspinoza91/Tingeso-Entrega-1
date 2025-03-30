package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, Long> {
}
