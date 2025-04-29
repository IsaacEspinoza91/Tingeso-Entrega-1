package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ReporteIngresosPersonasDTO;
import com.kartingRM.AppKartingRM.entities.ReporteIngresosVueltasDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private ReservaService reservaService;


    // Generar Reporte de ingresos segun vueltas (plan)
    public List<ReporteIngresosVueltasDTO> generarReporteIngresosVueltas(int mesInicio, int anioInicio,
                                                                         int mesFin, int anioFin) {

        // Validacion de que el rango de fechas mes anio es correcto
        validarRangoFechas(mesInicio, anioInicio, mesFin, anioFin);

        // Obtiene los ingresos segun plan utilizando la query en la base de datos
        List<Object[]> resultados = reservaService.findIngresosByVueltasAndFlexibleRange(
                mesInicio, anioInicio, mesFin, anioFin);

        return procesarResultadosReporteIngresosVueltas(resultados, mesInicio, anioInicio, mesFin, anioFin);
    }



    // Funcion privada que procesa los datos para obtener los ingresos por plan de forma estructurada.
    //   Considera el caso en que no hayan ingresos en algun mes y toma ingresos 0. Ademas de generar los
    //   nombres de los meses y no numeros.
    private List<ReporteIngresosVueltasDTO> procesarResultadosReporteIngresosVueltas(List<Object[]> resultados,
                                                                                     int mesInicio, int anioInicio,
                                                                                     int mesFin, int anioFin) {

        // Crear lista con Strings de nombres de mese-anio entre dos fechas
        List<String> mesesEnRango = generarMesesEnRango(mesInicio, anioInicio, mesFin, anioFin);

        // Obtener descripcion de todos los planes
        Set<String> planes = resultados.stream().map(r -> (String) r[0]).collect(Collectors.toSet());

        // Procesos para generar ingresos por mes, considerando el nombre del mes y el anio; ademas
        //   de meses sin ingresos con valores 0
        Map<String, ReporteIngresosVueltasDTO> reporteMap = new LinkedHashMap<>();

        // Inicializar todos los planes con ingresos por mes
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
    public List<String> generarMesesEnRango(int mesInicio, int anioInicio, int mesFin, int anioFin) {
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
    public String obtenerNombreMes(int mes) {
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
    public void validarRangoFechas(int mesInicio, int anioInicio, int mesFin, int anioFin) {
        if (anioInicio > anioFin || (anioInicio == anioFin && mesInicio > mesFin)) {
            throw new IllegalArgumentException("Rango de fechas inválido. El inicio debe ser anterior al fin.");
        }
        if (mesInicio < 1 || mesInicio > 12 || mesFin < 1 || mesFin > 12) {
            throw new IllegalArgumentException("Los meses deben estar entre 1 (Enero) y 12 (Diciembre)");
        }
    }



    // Generar Reporte de ingresos segun grupos de personas
    public List<ReporteIngresosPersonasDTO> generarReporteIngresosPorPersonas(int mesInicio, int yearInicio,
                                                                              int mesFin, int yearFin) {
        // Validamos que las fechas ingresadas sean validas temporalmente
        validarRangoFechas(mesInicio, yearInicio, mesFin, yearFin);
        List<Object[]> resultados = reservaService.findIngresosByRangoPersonas(mesInicio, yearInicio, mesFin, yearFin);
        return procesarResultadosReporteIngresosPersonas(resultados, mesInicio, yearInicio, mesFin, yearFin);
    }


    // Funcion privada que procesa los datos para obtener los ingresos por cantidad de personas de forma estructurada.
    //   Considera el caso de meses con ingresos 0. Ademas de generar los nombres de los meses y no numeros.
    private List<ReporteIngresosPersonasDTO> procesarResultadosReporteIngresosPersonas(List<Object[]> resultados,
                                                                                       int mesInicio, int anioInicio, int mesFin, int anioFin) {
        // Generar Lista de String con nombres de mes-anio entre dos fechas
        List<String> mesesEnRango = generarMesesEnRango(mesInicio, anioInicio, mesFin, anioFin);

        // Definir los rangos de personas segun cantidad de forma creciente
        Map<String, Predicate<Integer>> rangos = new LinkedHashMap<>();
        rangos.put("1-2 personas", cant -> cant >= 1 && cant <= 2);
        rangos.put("3-5 personas", cant -> cant >= 3 && cant <= 5);
        rangos.put("6-10 personas", cant -> cant >= 6 && cant <= 10);
        rangos.put("11-15 personas", cant -> cant >= 11 && cant <= 15);

        // Inicializar estructura
        Map<String, ReporteIngresosPersonasDTO> reporteMap = new LinkedHashMap<>();
        rangos.keySet().forEach(rango -> {
            Map<String, Double> ingresosPorMes = new LinkedHashMap<>();
            mesesEnRango.forEach(mes -> ingresosPorMes.put(mes, 0.0));
            reporteMap.put(rango, new ReporteIngresosPersonasDTO(rango, ingresosPorMes, 0.0, false));
        });

        // Llenar con los datos reales obtenidos de la base de datos
        for (Object[] resultado : resultados) {
            int cantidadPersonas = (int) resultado[0];
            String mesKey = obtenerNombreMes((int) resultado[1]) + "-" + resultado[2];
            double total = ((Number) resultado[3]).doubleValue();

            // Determinar a que rango pertenece
            for (Map.Entry<String, Predicate<Integer>> entry : rangos.entrySet()) {
                if (entry.getValue().test(cantidadPersonas)) {
                    // Calcular total de la fila (cantidad personas)
                    ReporteIngresosPersonasDTO dto = reporteMap.get(entry.getKey());
                    dto.getIngresosPorMes().merge(mesKey, total, Double::sum);
                    dto.setTotal(dto.getTotal() + total);
                    break;
                }
            }
        }

        // Calcular total general
        Map<String, Double> totalesPorMes = new LinkedHashMap<>();
        // Inicializamos HashMap con los meses y con montos 0
        mesesEnRango.forEach(mes -> totalesPorMes.put(mes, 0.0));

        // Calculamos los totales generales segun meses
        reporteMap.values().forEach(dto -> {
            dto.getIngresosPorMes().forEach((mes, valor) -> {
                totalesPorMes.merge(mes, valor, Double::sum);
            });
        });

        double granTotal = totalesPorMes.values().stream().mapToDouble(Double::doubleValue).sum();

        // Crear reporte final
        List<ReporteIngresosPersonasDTO> reporteFinal = new ArrayList<>(reporteMap.values());
        reporteFinal.add(new ReporteIngresosPersonasDTO(
                "TOTAL GENERAL", totalesPorMes, granTotal, true));

        return reporteFinal;
    }
}
