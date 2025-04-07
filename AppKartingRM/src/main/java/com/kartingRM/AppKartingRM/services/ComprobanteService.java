package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.repositories.ComprobanteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComprobanteService {

    @Autowired
    private ComprobanteRepository comprobanteRepository;
    @Autowired
    private ReservaService reservaService;

    public List<ComprobanteEntity> getComprobantes() {
        return comprobanteRepository.findAll();
    }

    public ComprobanteEntity getComprobanteById(Long id) {
        return comprobanteRepository.findById(id).get();
    }

    public ComprobanteEntity createComprobante(ComprobanteEntity comprobante, Long idReserva) {
        ReservaEntity reserva = reservaService.getReservaById(idReserva);
        if (reserva != null) {
            comprobante.setReserva(reserva);
            return comprobanteRepository.save(comprobante);
        } else {
            throw new RuntimeException("Reserva no encontrada");
        }
    }

    public ComprobanteEntity updateComprobante(Long id, ComprobanteEntity comprobante) {
        ComprobanteEntity comprobanteOriginal = comprobanteRepository.findById(id).get();
        comprobante.setIdComprobante(id);
        comprobante.setReserva(comprobanteOriginal.getReserva());
        return comprobanteRepository.save(comprobante);
    }

    public ComprobanteEntity updateReservaDeComprobante(Long id, Long idReserva) {
        ComprobanteEntity comprobanteOriginal = comprobanteRepository.findById(id).get();
        ReservaEntity reserva = reservaService.getReservaById(idReserva);

        comprobanteOriginal.setReserva(reserva);
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


    // Calcular el atributo total de Comprobante segun el los detalles de un comprobante especifico                                 OJOOJOJOJO
    @Transactional
    public void actualizarTotalComprobante(Long idComprobante) {
        ComprobanteEntity comprobante = getComprobanteById(idComprobante);
        int total = comprobante.getDetalles().stream()
                .mapToInt(DetalleComprobanteEntity::getMontoFinal)
                .sum();
        comprobante.setTotal(total);
        comprobanteRepository.save(comprobante);
    }
}
