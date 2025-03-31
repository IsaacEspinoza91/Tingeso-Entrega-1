package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<ReservaEntity> getReservas(){
        return reservaRepository.findAll();
    }

    public ReservaEntity getReserva(Long id){
        return reservaRepository.findById(id).get();
    }

    public ReservaEntity createReserva(ReservaEntity reserva){
        return reservaRepository.save(reserva);
    }

    public ReservaEntity updateReserva(Long id, ReservaEntity reserva){
        reserva.setIdReserva(id);
        return reservaRepository.save(reserva);
    }
    public boolean deteleReserva(Long id) throws Exception{
        try{
            reservaRepository.deleteById(id);
            return true;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
