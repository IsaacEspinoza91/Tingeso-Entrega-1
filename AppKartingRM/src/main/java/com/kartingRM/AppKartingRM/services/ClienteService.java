package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.repositories.ClienteRepository;
import com.kartingRM.AppKartingRM.repositories.DetalleComprobanteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Utilizamos el repositorio solo para obtener un dato particular. No realiza ninguna operacion de modificacion
    //  ni similares. Por lo que no se entiende como mala practica (Evita recursion de dependencias)
    @Autowired
    private DetalleComprobanteRepository detalleComprobanteRepository;

    public ArrayList<ClienteEntity> getClientes(){
        return (ArrayList<ClienteEntity>) clienteRepository.findAll();
    }

    public ClienteEntity getClienteById(Long id){
        return clienteRepository.findById(id).get();
    }

    public ClienteEntity getClienteByRut(String rut){
        return clienteRepository.findByRut(rut);
    }

    public ArrayList<ClienteEntity> findByNombreAndApellido(String nombre, String apellido){
        // Considera el caso en que haya mas de un cliente con el mismo nombre
        return clienteRepository.findByNombreAndApellido(nombre,apellido);
    }

    public ClienteEntity createCliente(ClienteEntity cliente){
        return clienteRepository.save(cliente);
    }

    public ClienteEntity updateCliente(Long id, ClienteEntity cliente){
        cliente.setId(id);
        return clienteRepository.save(cliente);
    }

    public boolean deleteCliente(Long id) throws Exception {
        try{
            clienteRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }


    // Obtiene la cantidad de veces que el cliente ha visitado el karting segun el mes
    @Transactional(readOnly = true)
    public int obtenerVecesUtilizadoKarting(Long clienteId, int anio, int mes) {
        // Caso cliente inexistente
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntityNotFoundException("Cliente no encontrado");
        }
        // Caso mes incorrecto
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mes incorrecto. Debe estar entre 1 y 12");
        }
        return detalleComprobanteRepository.contarVisitasByClienteYMes(clienteId, anio, mes);
    }

    // Retorna un booleano que indica si el cliente esta de cumpleanios en una fecha especifica
    public boolean cumpleAnios(ClienteEntity cliente, LocalDate fecha){
        if (cliente.getFechaNacimiento().getMonth() == fecha.getMonth() &&
                cliente.getFechaNacimiento().getDayOfMonth() == fecha.getDayOfMonth()) {
            return true;
        } else {
            return false;
        }
    }
}
