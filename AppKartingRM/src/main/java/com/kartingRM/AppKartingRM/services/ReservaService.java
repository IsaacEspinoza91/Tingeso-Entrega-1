package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.entities.PlanEntity;
import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.repositories.ClienteRepository;
import com.kartingRM.AppKartingRM.repositories.PlanRepository;
import com.kartingRM.AppKartingRM.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PlanRepository planRepository;

    /*
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private PlanService planService;
     */

    public List<ReservaEntity> getReservas(){
        return reservaRepository.findAll();
    }

    public ReservaEntity getReserva(Long id){
        return reservaRepository.findById(id).get();
    }

    public ReservaEntity createReserva(ReservaEntity reserva, Long idCliente, Long idPlan){
        //PlanEntity plan = planService.getPlanById(idPlan);
        PlanEntity plan = planRepository.findById(idPlan).get();
        //ClienteEntity cliente = clienteService.getClienteById(idCliente);
        ClienteEntity cliente = clienteRepository.findById(idCliente).get();
        if (cliente != null && plan != null) {
            reserva.setReservante(cliente);
            reserva.setPlan(plan);
            return reservaRepository.save(reserva);
        } else {
            throw new RuntimeException("Cliente no encontrado");
        }
    }

    public ReservaEntity updateReserva(Long id, ReservaEntity reserva){
        // Obtiene el objeto Reserva sin modificar
        ReservaEntity reservaOriginal = reservaRepository.findById(id).get();
        // Seteamos a los valores referenciados FK de la reserva original
        reserva.setIdReserva(id);
        reserva.setPlan(reservaOriginal.getPlan());
        reserva.setReservante(reservaOriginal.getReservante());
        return reservaRepository.save(reserva);
    }

    public ReservaEntity updateClienteDeReserva(Long idReserva, Long idCliente){
        // Obtener objeto Reserva
        ReservaEntity reservaOriginal = reservaRepository.findById(idReserva).get();
        // Obtener objeto Cliente
        //ClienteEntity cliente = clienteService.getClienteById(idCliente);
        ClienteEntity cliente = clienteRepository.findById(idCliente).get();
        reservaOriginal.setReservante(cliente);
        return reservaRepository.save(reservaOriginal);
    }

    public ReservaEntity updatePlanDeReserva(Long idReserva, Long idPlan){
        // Obtener objeto Reserva
        ReservaEntity reservaOriginal = reservaRepository.findById(idReserva).get();
        // Obtener objeto Plan
        PlanEntity plan = planRepository.findById(idPlan).get();
        reservaOriginal.setPlan(plan);
        return reservaRepository.save(reservaOriginal);
    }

    public boolean deleteReserva(Long id) throws Exception{
        try{
            reservaRepository.deleteById(id);
            return true;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
