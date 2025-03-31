package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ComprobanteEntity;
import com.kartingRM.AppKartingRM.repositories.ComprobanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComprobanteService {

    @Autowired
    private ComprobanteRepository comprobanteRepository;

    public List<ComprobanteEntity> getComprobantes() {
        return comprobanteRepository.findAll();
    }

    public ComprobanteEntity getComprobanteById(Long id) {
        return comprobanteRepository.findById(id).get();
    }

    public ComprobanteEntity createComprobante(ComprobanteEntity comprobante) {
        return comprobanteRepository.save(comprobante);
    }

    public ComprobanteEntity updateComprobante(Long id, ComprobanteEntity comprobante) {
        comprobante.setIdComprobante(id);
        return comprobanteRepository.save(comprobante);
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
