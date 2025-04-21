package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.*;
import com.kartingRM.AppKartingRM.repositories.DetalleComprobanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetalleComprobanteService {

    @Autowired
    private DetalleComprobanteRepository detalleComprobanteRepository;
    @Autowired
    private ComprobanteService comprobanteService;
    @Autowired
    private ClienteService clienteService;

    // Obtener todos los detalles
    public List<DetalleComprobanteEntity> getDetalleComprobantes() {
        return detalleComprobanteRepository.findAll();
    }

    // Obtener detalles por comprobante segun id
    public List<DetalleComprobanteEntity> getDetallesByComprobante(Long idComprobante) {
        return detalleComprobanteRepository.findByComprobanteIdComprobante(idComprobante);
    }

    // Obtener detalle especifico segun id
    public DetalleComprobanteEntity getDetalleComprobanteById(Long id) {
        return detalleComprobanteRepository.findById(id).get();
    }

    // Obtener todos los detallesComprobantes de un cliente
    public List<DetalleComprobanteEntity> getDetalleComprobantesByClienteId(Long clienteId) {
        return detalleComprobanteRepository.findByClienteId(clienteId);
    }

    // Obtener el detalleComprobante de un cliente y comprobante especifo
    public DetalleComprobanteEntity getDetalleComprobanteByClienteIdAndComprobanteId(Long clienteId, Long comprobanteId) {
        return detalleComprobanteRepository.findByClienteIdAndComprobanteIdComprobante(clienteId, comprobanteId);
    }

    // Crear nuevo detalle
    @Transactional
    public DetalleComprobanteEntity createDetalleComprobante(DetalleComprobanteEntity detalle, Long idComprobante, Long idCliente) {
        // Obtengo el comprobante segun la id
        ComprobanteEntity comprobante = comprobanteService.getComprobanteById(idComprobante);
        // Obtengo el cliente segun la id
        ClienteEntity cliente = clienteService.getClienteById(idCliente);

        // Configurar relación bidireccional Comprobante
        detalle.setComprobante(comprobante);
        // Configurar Relación unidireccional Cliente
        detalle.setCliente(cliente);

        DetalleComprobanteEntity detalleGuardado = detalleComprobanteRepository.save(detalle);

        // Actualizar total del comprobante al crear un nuevo detalle
        //comprobanteService.actualizarTotalComprobante(idComprobante); // Notar que la lista de detalles es vacia, por lo que no actualiza el total

        return detalleGuardado;
    }

    // Actualizar detalle existente
    @Transactional
    public DetalleComprobanteEntity updateDetalle(Long id, DetalleComprobanteEntity detalle) {
        DetalleComprobanteEntity detalleOriginal = detalleComprobanteRepository.findById(id).get();

        // Actualizar campos de detalle
        detalle.setIdDetalle(id);
        detalle.setCliente(detalleOriginal.getCliente());
        detalle.setComprobante(detalleOriginal.getComprobante());

        // Actualizar total del comprobante
        comprobanteService.actualizarTotalComprobante(detalle.getComprobante().getIdComprobante());

        return detalleComprobanteRepository.save(detalle);
    }


    // Actualizar detalle existente
    @Transactional
    public DetalleComprobanteEntity updateClienteDeDetalle(Long id, Long idCliente) {
        DetalleComprobanteEntity detalleOriginal = detalleComprobanteRepository.findById(id).get();

        ClienteEntity cliente = clienteService.getClienteById(idCliente);

        detalleOriginal.setCliente(cliente);

        return detalleComprobanteRepository.save(detalleOriginal);
    }


    // Eliminar detalle
    @Transactional
    public boolean deleteDetalleComprobante(Long id) throws Exception{
        try {
            // Obtener el detalle segun la id
            DetalleComprobanteEntity detalle = getDetalleComprobanteById(id);

            // Obtener comprobante de un DetalleComprobante
            ComprobanteEntity comprobante = detalle.getComprobante();

            // Elimina el detalle de la lista de detalles de Comprobante
            comprobante.getDetalles().remove(detalle);
            detalle.setComprobante(null);

            // Actualizar el total del comprobante
            comprobanteService.actualizarTotalComprobante(comprobante.getIdComprobante());

            // Elimina el detalle de la base de datos
            detalleComprobanteRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    @Transactional
    public DetalleComprobanteEntity guardarDetalle(DetalleComprobanteEntity detalle) {
        return detalleComprobanteRepository.save(detalle);
    }


}
