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


    // Query para obtener los ingresos segun plan en un rango de tiempo. Es utiliza para obtener el reporte de ingresos segun plan
    @Query("SELECT p.descripcion, MONTH(r.fecha) as mes, YEAR(r.fecha) as anio, SUM(c.total) as total " +
            "FROM ReservaEntity r " +
            "JOIN r.plan p " +
            "JOIN ComprobanteEntity c ON c.reserva = r " +
            "WHERE YEAR(r.fecha) > :yearInicio OR (YEAR(r.fecha) = :yearInicio AND MONTH(r.fecha) >= :mesInicio) " +
                   "AND YEAR(r.fecha) < :yearFin OR (YEAR(r.fecha) = :yearFin AND MONTH(r.fecha) <= :mesFin) " +
            "GROUP BY p.descripcion, YEAR(r.fecha), MONTH(r.fecha) " +
            "ORDER BY YEAR(r.fecha), MONTH(r.fecha)")
    List<Object[]> findIngresosByVueltasAndFlexibleRange(@Param("mesInicio") int mesInicio,
                                                         @Param("yearInicio") int yearInicio,
                                                         @Param("mesFin") int mesFin,
                                                         @Param("yearFin") int yearFin);
}
