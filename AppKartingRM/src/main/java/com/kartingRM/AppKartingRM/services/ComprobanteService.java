package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.entities.ComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.PlanEntity;
import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.repositories.ComprobanteRepository;
import com.kartingRM.AppKartingRM.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComprobanteService {

    @Autowired
    private ComprobanteRepository comprobanteRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    public List<ComprobanteEntity> getComprobantes() {
        return comprobanteRepository.findAll();
    }

    public ComprobanteEntity getComprobanteById(Long id) {
        return comprobanteRepository.findById(id).get();
    }

    public ComprobanteEntity createComprobante(ComprobanteEntity comprobante, Long idReserva) {
        ReservaEntity reserva = reservaRepository.findById(idReserva).get();
        if (reserva != null) {
            comprobante.setReservaEntity(reserva);
            return comprobanteRepository.save(comprobante);
        } else {
            throw new RuntimeException("Reserva no encontrada");
        }
    }

    public ComprobanteEntity updateComprobante(Long id, ComprobanteEntity comprobante) {
        ComprobanteEntity comprobanteOriginal = comprobanteRepository.findById(id).get();
        comprobante.setIdComprobante(id);
        comprobante.setReservaEntity(comprobanteOriginal.getReservaEntity());
        return comprobanteRepository.save(comprobante);
    }

    public ComprobanteEntity updateReservaDeComprobante(Long id, Long idReserva) {
        ComprobanteEntity comprobanteOriginal = comprobanteRepository.findById(id).get();
        ReservaEntity reserva = reservaRepository.findById(idReserva).get();

        comprobanteOriginal.setReservaEntity(reserva);
        return comprobanteRepository.save(comprobanteOriginal);
    }

    public boolean deleteComprobante(Long id) throws Exception{
        try{
            comprobanteRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
