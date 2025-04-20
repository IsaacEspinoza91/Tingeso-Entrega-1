package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.entities.ComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteId;
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

    // Obtener detalle especifico segun id compuesta
    public DetalleComprobanteEntity getDetalleComprobanteById(DetalleComprobanteId id) {
        return detalleComprobanteRepository.findById(id).orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
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
        ComprobanteEntity comprobante = comprobanteService.getComprobanteById(idComprobante);
        ClienteEntity cliente = clienteService.getClienteById(idCliente);

        // Configurar relación bidireccional Comprobante
        detalle.setComprobante(comprobante);                    // Add el comprobante al detalle
        DetalleComprobanteId id = new DetalleComprobanteId();   // Creamos nueva id del detalle
        id.setIdComprobante(idComprobante);
        id.setIdDetalle(generarNuevoIdDetalle(idComprobante));
        detalle.setIdDetalle(id);                               // Setemos id al detalle

        // Configurar Relación unidireccional Cliente
        detalle.setCliente(cliente);

        DetalleComprobanteEntity detalleGuardado = detalleComprobanteRepository.save(detalle);

        // Actualizar total del comprobante                                                                                        OJOOOJOJOJO
        comprobanteService.actualizarTotalComprobante(idComprobante);

        return detalleGuardado;
    }

    // Actualizar detalle existente
    @Transactional
    public DetalleComprobanteEntity updateDetalle(DetalleComprobanteId id, DetalleComprobanteEntity detalle, Long idCliente) {
        DetalleComprobanteEntity detalleExistente = getDetalleComprobanteById(id);
        ClienteEntity cliente = clienteService.getClienteById(idCliente);

        // Actualizar campos de detalle
        detalleExistente.setTarifa(detalle.getTarifa());
        detalleExistente.setDescuentoGrupo(detalle.getDescuentoGrupo());
        detalleExistente.setDescuentoEspecial(detalle.getDescuentoEspecial());
        detalleExistente.setMontoTotal(detalle.getMontoTotal());
        detalleExistente.setMontoIva(detalle.getMontoIva());
        detalleExistente.setMontoFinal(detalle.getMontoFinal());
        detalleExistente.setCliente(cliente);

        DetalleComprobanteEntity detalleActualizado = detalleComprobanteRepository.save(detalleExistente);

        // Actualizar total del comprobante
        comprobanteService.actualizarTotalComprobante(id.getIdComprobante());

        return detalleActualizado;
    }

    // Eliminar detalle
    @Transactional
    public void deleteDetalleComprobante(Long idDetalle, Long idComprobante) {
        // Generar la Id compuesta del detalle
        DetalleComprobanteId id = new DetalleComprobanteId(idDetalle, idComprobante);
        // Obtener el detalle segun la id compuesta
        DetalleComprobanteEntity detalle = getDetalleComprobanteById(id);

        // Obtner comprobante de un DetalleComprobante
        ComprobanteEntity comprobante = detalle.getComprobante();

        // Elimina el detalle de la lista de detalles de Comprobante
        comprobante.getDetalles().remove(detalle);
        detalle.setComprobante(null);

        // Eliminar detalle
        detalleComprobanteRepository.delete(detalle);

        // Actualizar el total del comprobante
        comprobanteService.actualizarTotalComprobante(comprobante.getIdComprobante());
    }


    // Metodo auxiliar para generar nuevo ID de detalle
    //   consider la llave compuesta donde se mantiene la id comprobante y la de detalle va aumentando en 1
    private Long generarNuevoIdDetalle(Long idComprobante) {
        // Encontrar el id maximo dentro de la tabla DetallesComprobantes
        Long maxId = detalleComprobanteRepository.findMaxIdDetalleByIdComprobante(idComprobante);
        // En caso de no tener maximo, retorno 1; en caso contrario sumo 1 al maximo y lo retorno
        return maxId == null ? 1L : maxId + 1;
    }

}
