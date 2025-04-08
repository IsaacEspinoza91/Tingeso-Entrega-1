package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.ReservaKartEntity;
import com.kartingRM.AppKartingRM.entities.ReservaKartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaKartRepository extends JpaRepository<ReservaKartEntity, ReservaKartId> {

    // Encontrar todos los karts segun la id de una reserva
    List<ReservaKartEntity> findByReservaIdReserva(Long idReserva);

    // Encontrar todas las reservas segun la id de un kart
    List<ReservaKartEntity> findByKartIdkart(Long idKart);

    // Verificar si existe una relación entre una reserva y un kart segun ambas ids
    boolean existsByReservaIdReservaAndKartIdkart(Long idReserva, Long idKart);

    void deleteByReservaIdReserva(Long idReserva);
}
