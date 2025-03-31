package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ArrayList<ClienteEntity> getClientes(){
        return (ArrayList<ClienteEntity>) clienteRepository.findAll();
    }

    public ClienteEntity getClienteById(Long id){
        return clienteRepository.findById(id).get();
    }

    public ClienteEntity getClienteByRut(String rut){
        return clienteRepository.findByRut(rut);
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


}
