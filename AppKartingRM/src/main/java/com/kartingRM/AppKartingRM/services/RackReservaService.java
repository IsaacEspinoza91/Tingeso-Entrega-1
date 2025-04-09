package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.RackSemanalDTO;
import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.entities.ReservaRackDTO;
import com.kartingRM.AppKartingRM.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class RackReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public RackSemanalDTO obtenerRackSemanal(int semanaOffset) {
        // Obtenemos el dia lunes de la semana a obtener el rack
        LocalDate lunes = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .plusWeeks(semanaOffset);
        // Obtenemos el dia domingo de la semana a obtener el rack
        LocalDate domingo = lunes.plusDays(6);

        // Obtenemos lista con reservas del tipo confirmadas en un rango de fechas lunes-domingo
        List<ReservaEntity> reservas = reservaRepository.findReservasConfirmadasPorRangoFechas(lunes, domingo);

        // Inicializamos el HashMap de las reservas por dia
        Map<String, List<ReservaRackDTO>> reservasPorDia = new LinkedHashMap<>();

        // Inicializar el HashMap con los nombres de días de la semana
        for (int i = 0; i < 7; i++) {
            String nombreDia = lunes.plusDays(i)  // suma i dias al lunes de la semana
                    .getDayOfWeek()                 // Obtiene que dia es el sumada
                    .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));    // Obtiene nombre del dia en español
            reservasPorDia.put(nombreDia, new ArrayList<>());
        }

        // Obtener elemento reservasPorDia. Genera lista con las reservas por dias en un HashMap reservas
        reservas.forEach(reserva -> {
            // Obtiene el nombre del dia segun reserva
            String nombreDia = reserva.getFecha()
                    .getDayOfWeek()
                    // Convierte el nombre del dia a español
                    .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

            reservasPorDia.get(nombreDia).add(
                    // Generamos el objeto ReservaRackDTO de las reservas para cada dia
                    new ReservaRackDTO(
                            reserva.getIdReserva().toString(),
                            reserva.getReservante().getNombre() + " " + reserva.getReservante().getApellido(),
                            reserva.getHoraInicio(),
                            reserva.getHoraFin()
                    )
            );
        });

        // Retornamos elmeneto RackSemanal para
        return new RackSemanalDTO(lunes, domingo, reservasPorDia);
    }
}