package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.entities.PlanEntity;
import com.kartingRM.AppKartingRM.entities.ReporteIngresosVueltasDTO;
import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<ReservaEntity> getReservasSolapadasByHoraInicio(Date fecha, LocalTime horaInicio, LocalTime horaFinal){
        return reservaRepository.findReservasExistentesEnTiempo(fecha, horaInicio, horaFinal);
    }

    // Funcion que ingresa una reserva a la base de datos
    // Considera que no existan reservas en el horario nuevo, ademas de que se ingresa automaticamente
    //    la hora de fin segun el tiempo del plan
    public ReservaEntity createReserva(ReservaEntity reserva, Long idCliente, Long idPlan){
        PlanEntity plan = planService.getPlanById(idPlan);
        ClienteEntity cliente = clienteService.getClienteById(idCliente);
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

    // Actualizar reserva
    public ReservaEntity updateReserva(Long id, ReservaEntity reserva){
        // Obtiene el objeto Reserva sin modificar
        ReservaEntity reservaOriginal = reservaRepository.findById(id).get();
        // Seteamos a los valores referenciados FK de la reserva original
        reserva.setIdReserva(id);
        reserva.setPlan(reservaOriginal.getPlan());
        reserva.setReservante(reservaOriginal.getReservante());
        return reservaRepository.save(reserva);
    }

    // Actualizar cliente de una reserva
    public ReservaEntity updateClienteDeReserva(Long idReserva, Long idCliente){
        // Obtener objeto Reserva
        ReservaEntity reservaOriginal = reservaRepository.findById(idReserva).get();
        // Obtener objeto Cliente
        ClienteEntity cliente = clienteService.getClienteById(idCliente);
        reservaOriginal.setReservante(cliente);
        return reservaRepository.save(reservaOriginal);
    }

    // Actualizar plan de una reserva
    public ReservaEntity updatePlanDeReserva(Long idReserva, Long idPlan){
        // Obtener objeto Reserva
        ReservaEntity reservaOriginal = reservaRepository.findById(idReserva).get();
        // Obtener objeto Plan
        PlanEntity plan = planService.getPlanById(idPlan);
        reservaOriginal.setPlan(plan);
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





    // Generar Reporte de ingresos segun vueltas (plan)
    public List<ReporteIngresosVueltasDTO> generarReporteIngresosVueltas(int mesInicio, int anioInicio,
                                                                         int mesFin, int anioFin) {

        // Validacion de que el rango de fechas mes anio es correcto
        validarRangoFechas(mesInicio, anioInicio, mesFin, anioFin);

        // Obtiene los ingresos segun plan utilizando la query en la base de datos
        List<Object[]> resultados = reservaRepository.findIngresosByVueltasAndFlexibleRange(
                mesInicio, anioInicio, mesFin, anioFin);

        return procesarResultados(resultados, mesInicio, anioInicio, mesFin, anioFin);
    }



    // Funcion privada que procesa los datos para obtener los ingresos por plan de forma estructurada.
    //   Considera el caso en que no hayan ingresos en algun mes y toma ingresos 0. Ademas de generar los
    //   nombres de los meses y no numeros.
    private List<ReporteIngresosVueltasDTO> procesarResultados(List<Object[]> resultados,
                                                               int mesInicio, int anioInicio,
                                                               int mesFin, int anioFin) {

        // Crear lista con Strings de nombres de mese-anio entre dos fechas
        List<String> mesesEnRango = generarMesesEnRango(mesInicio, anioInicio, mesFin, anioFin);

        // Obtener descripcion de todos los planes
        Set<String> planes = resultados.stream().map(r -> (String) r[0]).collect(Collectors.toSet());

        // Procesos para generar ingresos por mes, considerando el nombre del mes y el anio; ademas
        //   de meses sin ingresos con valores 0
        Map<String, ReporteIngresosVueltasDTO> reporteMap = new LinkedHashMap<>();

        // Inicializar todos los planes con meses en 0 de ingresos
        planes.forEach(plan -> {
            Map<String, Double> ingresosPorMes = new LinkedHashMap<>();
            mesesEnRango.forEach(mes -> ingresosPorMes.put(mes, 0.0));
            reporteMap.put(plan, new ReporteIngresosVueltasDTO(plan, ingresosPorMes, 0.0, false));
        });

        // Llenar con los datos reales obtenidos de la base de datos
        for (Object[] resultado : resultados) {
            String descripcionPlan = (String) resultado[0];
            // Obtener string con mes-anio
            String claveMes = obtenerNombreMes((int) resultado[1]) + "-" + resultado[2];
            double totalMes = ((Number) resultado[3]).doubleValue();

            ReporteIngresosVueltasDTO dto = reporteMap.get(descripcionPlan);
            dto.getIngresosPorMes().put(claveMes, totalMes);
            dto.setTotal(dto.getTotal() + totalMes);
        }

        // Proceso para crea fila final de Totales de meses y total general
        Map<String, Double> totalesPorMes = new LinkedHashMap<>();
        // inicializamos meses con valores 0
        mesesEnRango.forEach(mes -> totalesPorMes.put(mes, 0.0));

        // Calcular total ingresos por mes para todos los planes
        reporteMap.values().forEach(dto -> {
            dto.getIngresosPorMes().forEach((mes, valor) -> {
                totalesPorMes.put(mes, totalesPorMes.get(mes) + valor);
            });
        });

        // Calcular gran total sumando ingresos de todos los meses
        double granTotal = totalesPorMes.values().stream().mapToDouble(Double::doubleValue).sum();

        // Crear DaraTransferObject de valores totales
        ReporteIngresosVueltasDTO totalGeneral = new ReporteIngresosVueltasDTO(
                "TOTAL GENERAL", totalesPorMes, granTotal, true);

        // Crear lista de registros de ReporteIngresos y agregar fila de TOTAL GENERAL
        List<ReporteIngresosVueltasDTO> reporteFinal = new ArrayList<>(reporteMap.values());
        reporteFinal.add(totalGeneral);

        return reporteFinal;
    }


    // Funcion privada para obtener una lista con los nombres de mes-anio entre dos fechas
    //   Es utilizada para generar reportes de ingresos. Resulta util para casos donde los ingresos sean 0 y
    //   no se encuentre registros en la base de datos para obtener su nombre
    private List<String> generarMesesEnRango(int mesInicio, int anioInicio, int mesFin, int anioFin) {
        List<String> meses = new ArrayList<>();
        LocalDate fechaInicio = LocalDate.of(anioInicio, mesInicio, 1);
        LocalDate fechaFin = LocalDate.of(anioFin, mesFin, 1);

        for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusMonths(1)) {
            meses.add(obtenerNombreMes(fecha.getMonthValue()) + "-" + fecha.getYear());
        }

        return meses;
    }


    // Funcion privada que obtiene el nombre de un mes segun su numero
    //   Es utilizada dentro para generar reportes de datos
    private String obtenerNombreMes(int mes) {
        if (mes > 0 && mes < 13) {
            String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            return nombresMeses[mes - 1];
        } else {
            throw new IllegalArgumentException("Mes inválido: " + mes);
        }
    }


    // Funcion privada para validar y los rangos de dos fecha de inicio y fin (mes y anio) son validas
    //    Es utilizada como condicion al generar reportes
    private void validarRangoFechas(int mesInicio, int anioInicio, int mesFin, int anioFin) {
        if (anioInicio > anioFin || (anioInicio == anioFin && mesInicio > mesFin)) {
            throw new IllegalArgumentException("Rango de fechas inválido. El inicio debe ser anterior al fin.");
        }
        if (mesInicio < 1 || mesInicio > 12 || mesFin < 1 || mesFin > 12) {
            throw new IllegalArgumentException("Los meses deben estar entre 1 (Enero) y 12 (Diciembre)");
        }
    }
}
