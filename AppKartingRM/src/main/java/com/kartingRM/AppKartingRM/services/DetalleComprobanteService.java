package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteId;
import com.kartingRM.AppKartingRM.repositories.DetalleComprobanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleComprobanteService {

    @Autowired
    private DetalleComprobanteRepository detalleComprobanteRepository;

    public List<DetalleComprobanteEntity> obtenerDetalleComprobantes() {
        return detalleComprobanteRepository.findAll();
    }

    public DetalleComprobanteEntity obtenerDetalleComprobanteById(DetalleComprobanteId id) {
        return detalleComprobanteRepository.findById(id).get();
    }

    public DetalleComprobanteEntity createDetalleComprobante(DetalleComprobanteEntity detalleComprobante) {
        return detalleComprobanteRepository.save(detalleComprobante);
    }

    public DetalleComprobanteEntity updateDetalleComprobante(DetalleComprobanteId id,DetalleComprobanteEntity detalleComprobante) {
        detalleComprobante.setIdDetalle(id);
        return detalleComprobanteRepository.save(detalleComprobante);
    }

    public boolean deleteDetalleComprobante(DetalleComprobanteId id) throws Exception{
        try{
            detalleComprobanteRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
