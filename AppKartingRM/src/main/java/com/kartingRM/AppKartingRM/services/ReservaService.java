package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.*;
import com.kartingRM.AppKartingRM.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private PlanService planService;
    @Autowired
    private ClienteService clienteService;


    // Obtener todas las reservas
    public List<ReservaEntity> getReservas(){
        return reservaRepository.findAll();
    }

    // Obtener reserva segun id
    public ReservaEntity getReservaById(Long id){
        return reservaRepository.findById(id).get();
    }

    // Obtener todas las reservas segun id de un cliente reservante
    public List<ReservaEntity> getReservasByClienteId(Long clienteId) {
        return reservaRepository.findByReservanteId(clienteId);
    }

    // Obtener reservas existentes entre dos horas de un dia
    public List<ReservaEntity> getReservasSolapadasByHoraInicio(LocalDate fecha, LocalTime horaInicio, LocalTime horaFinal){
        return reservaRepository.findReservasExistentesEnTiempo(fecha, horaInicio, horaFinal);
    }

    // Obtener integrantes de reserva segun id
    public List<ClienteEntity> getIntegrantesById(Long id) {
        ReservaEntity reserva = reservaRepository.findById(id).get();   // Obtengo reserva
        // Entrego los integrantes
        return reserva.getIntegrantes();
    }

    // Funcion que ingresa una reserva a la base de datos
    // Considera que no existan reservas en el horario nuevo, ademas de que se ingresa automaticamente
    //    la hora de fin segun el tiempo del plan
    public ReservaEntity createReserva(ReservaEntity reserva, Long idCliente, Long idPlan, Boolean esFeriado){
        PlanEntity plan = planService.getPlanById(idPlan);
        ClienteEntity cliente = clienteService.getClienteById(idCliente);
        // Caso parametros validos de cliente, plan y hora de inicio
        if (cliente != null && plan != null && reserva.getHoraInicio() != null) {
            reserva.setReservante(cliente);
            reserva.setPlan(plan);

            // Determinar la hora final sumando los minutos del plan a la hora inicial
            LocalTime horaInicio = reserva.getHoraInicio();
            LocalTime horaFinalCalculada = reserva.getHoraInicio().plusMinutes(reserva.getPlan().getDuracionTotal());
            reserva.setHoraFin(horaFinalCalculada);

            // Verificar que no existan reservas existentes en el horario determinado
            List<ReservaEntity> reservasExistentes = getReservasSolapadasByHoraInicio(
                    reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin());

            // Si existen reservas en el horario, no sea crea la nueva reserva
            if (!reservasExistentes.isEmpty()){
                throw new IllegalStateException("Ya existe una reserva con ese horario");
            }

            // Verificar Horario de inicio y fin validos.
            //  Lunes a Viernes: 14:00 a 22:00
            //  Sabados, Domingos, Feriados: 10:00 a 22:00
            boolean esFinDeSemana = reserva.getFecha().getDayOfWeek().getValue() >= 6;// Analisis si es fin de semana o no
            if (esFeriado || esFinDeSemana){// Horario fin de semana o feriado
                // Horario antes o despues del horario de servicio
                if (horaInicio.isBefore(LocalTime.of(10,00,00)) || horaFinalCalculada.isAfter(LocalTime.of(22,00,00))) {
                    throw new IllegalStateException("Horario incorrecto. Domingos, sábados y feriados: 10:00 a 22:00");
                }
            } else { // Horario semana
                if (horaInicio.isBefore(LocalTime.of(14,00,00)) || horaFinalCalculada.isAfter(LocalTime.of(22,00,00))) {
                    throw new IllegalStateException("Horario incorrecto. Lunes a Viernes: 14:00 a 22:00");
                }
            }

            return reservaRepository.save(reserva);
        } else {
            throw new RuntimeException("Cliente no encontrado, plan no existe, o hora erronea");
        }
    }

    // Actualizar reserva
    public ReservaEntity updateReserva(Long id, ReservaEntity reserva){
        // Obtiene el objeto Reserva sin modificar
        ReservaEntity reservaOriginal = reservaRepository.findById(id).get();
        // Seteamos a los valores referenciados FK de la reserva original
        reserva.setIdReserva(id);
        reserva.setPlan(reservaOriginal.getPlan());
        reserva.setReservante(reservaOriginal.getReservante());

        // Actualizacion nuevo hora de fin, caso cambio de plan o de hora de inicio
        LocalTime nuevaHoraFin = reserva.getHoraInicio().plusMinutes(reserva.getPlan().getDuracionTotal());
        reserva.setHoraFin(nuevaHoraFin);

        // Falta verificar si cambio el tipo de plan para actualizar los tiempos o el horario para actualizar el horario final
        return reservaRepository.save(reserva);
    }

    // Actualizar cliente de una reserva
    public ReservaEntity updateClienteDeReserva(Long idReserva, Long idCliente){
        // Obtener objeto Reserva
        ReservaEntity reservaOriginal = reservaRepository.findById(idReserva).get();
        // Obtener objeto Cliente
        ClienteEntity cliente = clienteService.getClienteById(idCliente);
        reservaOriginal.setReservante(cliente);

        // Actualizacion nuevo hora de fin, caso cambio de plan o de hora de inicio
        LocalTime nuevaHoraFin = reservaOriginal.getHoraInicio().plusMinutes(reservaOriginal.getPlan().getDuracionTotal());
        reservaOriginal.setHoraFin(nuevaHoraFin);
        return reservaRepository.save(reservaOriginal);
    }

    // Actualizar plan de una reserva
    public ReservaEntity updatePlanDeReserva(Long idReserva, Long idPlan){
        // Obtener objeto Reserva
        ReservaEntity reservaOriginal = reservaRepository.findById(idReserva).get();
        // Obtener objeto Plan
        PlanEntity plan = planService.getPlanById(idPlan);
        reservaOriginal.setPlan(plan);

        // Actualizacion nuevo hora de fin, caso cambio de plan o de hora de inicio
        LocalTime nuevaHoraFin = reservaOriginal.getHoraInicio().plusMinutes(reservaOriginal.getPlan().getDuracionTotal());
        reservaOriginal.setHoraFin(nuevaHoraFin);
        return reservaRepository.save(reservaOriginal);
    }

    // Eliminar una reserva segun su id
    public boolean deleteReserva(Long id) throws Exception{
        try{
            reservaRepository.deleteById(id);
            return true;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    // Obtiene los ingresos segun plan en un rango de tiempo
    public List<Object[]> findIngresosByVueltasAndFlexibleRange(int mesInicio, int yearInicio, int mesFin, int yearFin ){
        return reservaRepository.findIngresosByVueltasAndFlexibleRange(mesInicio, yearInicio, mesFin, yearFin);
    }

    // Obtiene los ingresos segun rango de personas en la reserva
    public List<Object[]> findIngresosByRangoPersonas(int mesInicio, int yearInicio, int mesFin, int yearFin ){
        return reservaRepository.findIngresosByRangoPersonas(mesInicio, yearInicio, mesFin, yearFin);
    }



    // Agregar un cliente a la lista de integrantes de una reserva
    public ReservaEntity agregarIntegrante(Long reservaId, Long clienteId) {
        ReservaEntity reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Caso en que se intentan agregar mas integrantes de la cantidad total de integrantes de la reserva
        if (reserva.getIntegrantes().size() >= reserva.getTotalPersonas()) {
            throw new IllegalStateException("Ya no se puede asignar mas integrantes");
        } else {
            // Caso en que todavia no se agregan todos los integrantes a la reserva
            // Obtener cliente segun id
            ClienteEntity cliente = clienteService.getClienteById(clienteId);

            // Si es que la reserva no tiene al cliente como integrante, se agrega, en caso contrario no
            if (!reserva.getIntegrantes().contains(cliente)) {
                reserva.getIntegrantes().add(cliente);
            }

            return reservaRepository.save(reserva);
        }
    }

    // Quitar un cliente segun id de la lista de integrantes de una reserva
    public ReservaEntity quitarIntegrante(Long reservaId, Long clienteId) {
        ReservaEntity reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        // Obtener cliente segun id
        ClienteEntity cliente = clienteService.getClienteById(clienteId);

        reserva.getIntegrantes().remove(cliente);

        return reservaRepository.save(reserva);
    }
}
