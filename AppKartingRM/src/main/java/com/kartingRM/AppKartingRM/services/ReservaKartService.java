package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.KartEntity;
import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.entities.ReservaKartEntity;
import com.kartingRM.AppKartingRM.entities.ReservaKartId;
import com.kartingRM.AppKartingRM.repositories.ReservaKartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaKartService {

    @Autowired
    private ReservaKartRepository reservaKartRepository;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private KartService kartService;

    // Obtener todas las relaciones reserva-kart
    public List<ReservaKartEntity> getAllReservaKarts() {
        return reservaKartRepository.findAll();
    }

    // Obtener karts asociados a una reserva
    public List<ReservaKartEntity> getKartsByReserva(Long idReserva) {
        return reservaKartRepository.findByReservaIdReserva(idReserva);
    }

    // Obtener reservas asociadas a un kart
    public List<ReservaKartEntity> getReservasByKart(Long idKart) {
        return reservaKartRepository.findByKartIdkart(idKart);
    }

    // Crear una nueva relación reserva-kart
    @Transactional
    public ReservaKartEntity createReservaKart(Long idReserva, Long idKart) {
        ReservaEntity reserva = reservaService.getReservaById(idReserva);
        KartEntity kart = kartService.getKartById(idKart);

        if (reserva == null || kart == null) {
            throw new RuntimeException("Reserva o Kart no encontrado");
        }

        // Verificar si el kart ya está asignado a esta reserva
        if (reservaKartRepository.existsByReservaIdReservaAndKartIdkart(idReserva, idKart)) {
            throw new IllegalStateException("El kart ya está asignado a esta reserva");
        }

        ReservaKartEntity reservaKart = new ReservaKartEntity();
        reservaKart.setReserva(reserva);
        reservaKart.setKart(kart);

        return reservaKartRepository.save(reservaKart);
    }

    // Eliminar una relación reserva-kart
    @Transactional
    public void deleteReservaKart(Long idReserva, Long idKart) {
        ReservaKartId id = new ReservaKartId();
        id.setReserva(idReserva);
        id.setKart(idKart);

        if (!reservaKartRepository.existsById(id)) {
            throw new RuntimeException("Relación Reserva-Kart no encontrada");
        }

        reservaKartRepository.deleteById(id);
    }

    // Eliminar todas las relaciones de una reserva
    @Transactional
    public void deleteAllByReserva(Long idReserva) {
        reservaKartRepository.deleteByReservaIdReserva(idReserva);
    }
}
