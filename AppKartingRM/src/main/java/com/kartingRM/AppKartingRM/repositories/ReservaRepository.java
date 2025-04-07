package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    List<ReservaEntity> findByReservanteId(Long id);

    @Query("SELECT r FROM ReservaEntity r WHERE r.fecha = :fecha AND (:horaInicio < r.horaFin AND :horaFin > r.horaInicio)")
    List<ReservaEntity> findReservasExistentesEnTiempo(@Param("fecha") Date fecha,
                                                       @Param("horaInicio") LocalTime horaInicio,
                                                       @Param("horaFin") LocalTime horaFin);
}
