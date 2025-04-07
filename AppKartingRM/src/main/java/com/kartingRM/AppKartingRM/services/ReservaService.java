package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.entities.PlanEntity;
import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.repositories.ClienteRepository;
import com.kartingRM.AppKartingRM.repositories.PlanRepository;
import com.kartingRM.AppKartingRM.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
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

    public ReservaEntity getReservaById(Long id){
        return reservaRepository.findById(id).get();
    }

    public List<ReservaEntity> getReservasByClienteId(Long clienteId) {
        return reservaRepository.findByReservanteId(clienteId);
    }

    public List<ReservaEntity> getReservasSolapadasByHoraInicio(Date fecha, LocalTime horaInicio, LocalTime horaFinal){
        return reservaRepository.findReservasExistentesEnTiempo(fecha, horaInicio, horaFinal);
    }

    // Funcion que ingresa una reserva a la base de datos
    // Considera que no existan reservas en el horario nuevo, ademas de que se ingresa automaticamente
    //    la hora de fin segun el tiempo del plan
    public ReservaEntity createReserva(ReservaEntity reserva, Long idCliente, Long idPlan){
        //PlanEntity plan = planService.getPlanById(idPlan);
        PlanEntity plan = planRepository.findById(idPlan).get();
        //ClienteEntity cliente = clienteService.getClienteById(idCliente);
        ClienteEntity cliente = clienteRepository.findById(idCliente).get();
        if (cliente != null && plan != null && reserva.getHoraInicio() != null) {
            reserva.setReservante(cliente);
            reserva.setPlan(plan);

            // Determinar la hora final sumando los minutos del plan a la hora inicial
            LocalTime horaFinalCalculada = reserva.getHoraInicio().plusMinutes(reserva.getPlan().getDuracionTotal());
            reserva.setHoraFin(horaFinalCalculada);

            // Verificar que no existan reservas existentes en el horario determinado
            List<ReservaEntity> reservasExistentes = getReservasSolapadasByHoraInicio(
                    reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin());
            // Si existen reservas en el horario, no sea crea la nueva reserva
            if (!reservasExistentes.isEmpty()){
                throw new IllegalStateException("Ya existe una reserva con ese horario");
            }
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
