package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ReporteIngresosPersonasDTO;
import com.kartingRM.AppKartingRM.entities.ReporteIngresosVueltasDTO;
import com.kartingRM.AppKartingRM.services.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReporteServiceTest {

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ReporteService reporteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generarReporteIngresosVueltas_rangoFechasValido_deberiaRetornarReporteIngresosVueltas() {
        // Given
        int mesInicio = 1;
        int anioInicio = 2023;
        int mesFin = 3;
        int anioFin = 2023;

        // Mock data
        List<Object[]> mockData = Arrays.asList(
                new Object[]{"Plan Básico", 1, 2023, 1000000.0},
                new Object[]{"Plan Básico", 2, 2023, 1500000.0},
        new Object[]{"Plan Premium", 1, 2023, 2000000.0}
        );

        when(reservaService.findIngresosByVueltasAndFlexibleRange(mesInicio, anioInicio, mesFin, anioFin))
                .thenReturn(mockData);

        // When
        List<ReporteIngresosVueltasDTO> result = reporteService.generarReporteIngresosVueltas(
                mesInicio, anioInicio, mesFin, anioFin);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size()); // 2 planes + 1 ultima fila

        // condionc plan
        ReporteIngresosVueltasDTO planBasico = result.stream()
                .filter(r -> r.getDescripcionPlan().equals("Plan Básico"))
                .findFirst()
                .orElse(null);
        assertNotNull(planBasico);
        assertEquals(2500000.0, planBasico.getTotal());
        assertEquals(1000000.0, planBasico.getIngresosPorMes().get("Enero-2023"));
        assertEquals(1500000.0, planBasico.getIngresosPorMes().get("Febrero-2023"));
        assertEquals(0.0, planBasico.getIngresosPorMes().get("Marzo-2023"));

        // condicion Total
        ReporteIngresosVueltasDTO totalGeneral = result.get(2);
        assertTrue(totalGeneral.isEsTotalGeneral());
        assertEquals(4500000.0, totalGeneral.getTotal());
    }

    @Test
    void generarReporteIngresosVueltas_rangoFechasInvalido_deberiaLanzarThrowException() {
        // Given
        int mesInicio = 5;
        int anioInicio = 2023;
        int mesFin = 2;
        int anioFin = 2023;

        // When y Then
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.generarReporteIngresosVueltas(mesInicio, anioInicio, mesFin, anioFin);
        });
    }

    @Test
    void generarReporteIngresosVueltas_mesInvalido_deberiaLanzarThrowException() {
        // Given
        int mesInicio = 0;
        int anioInicio = 2023;
        int mesFin = 12;
        int anioFin = 2023;

        // When y Then
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.generarReporteIngresosVueltas(mesInicio, anioInicio, mesFin, anioFin);
        });
    }

    @Test
    void generarReporteIngresosPorPersonas_rangoFechasValido_deberiaRetornarReporteIngresosPorPersonas() {
        // Given
        int mesInicio = 1;
        int anioInicio = 2023;
        int mesFin = 2;
        int anioFin = 2023;

        // Mock data
        List<Object[]> mockData = Arrays.asList(
                new Object[]{2, 1, 2023, 500000.0},
                new Object[]{4, 1, 2023, 800000.0},
                new Object[]{2, 2, 2023, 600000.0},
                new Object[]{8, 2, 2023, 1200000.0}
        );

        when(reservaService.findIngresosByRangoPersonas(mesInicio, anioInicio, mesFin, anioFin))
                .thenReturn(mockData);

        // When
        List<ReporteIngresosPersonasDTO> result = reporteService.generarReporteIngresosPorPersonas(
                mesInicio, anioInicio, mesFin, anioFin);

        // Then
        assertNotNull(result);
        assertEquals(5, result.size()); // 4 rangos + 1 total general

        // codicion 1-2 personas
        ReporteIngresosPersonasDTO rango1_2 = result.stream()
                .filter(r -> r.getRangoPersonas().equals("1-2 personas"))
                .findFirst()
                .orElse(null);
        assertNotNull(rango1_2);
        assertEquals(1100000.0, rango1_2.getTotal());
        assertEquals(500000.0, rango1_2.getIngresosPorMes().get("Enero-2023"));
        assertEquals(600000.0, rango1_2.getIngresosPorMes().get("Febrero-2023"));

        // codicion 3-5 personas
        ReporteIngresosPersonasDTO rango3_5 = result.stream()
                .filter(r -> r.getRangoPersonas().equals("3-5 personas"))
                .findFirst()
                .orElse(null);
        assertNotNull(rango3_5);
        assertEquals(800000.0, rango3_5.getTotal());
        assertEquals(800000.0, rango3_5.getIngresosPorMes().get("Enero-2023"));
        assertEquals(0.0, rango3_5.getIngresosPorMes().get("Febrero-2023"));

        // Total general, ultima fila
        ReporteIngresosPersonasDTO totalGeneral = result.get(4);
        assertTrue(totalGeneral.isEsTotalGeneral());
        assertEquals(3100000.0, totalGeneral.getTotal());
    }

    @Test
    void generarMesesEnRango_deberiaRetornarRangoDeMesesCorrecto() {
        // When
        List<String> result = reporteService.generarMesesEnRango(1, 2023, 3, 2023);

        // Then
        assertEquals(3, result.size());
        assertEquals("Enero-2023", result.get(0));
        assertEquals("Febrero-2023", result.get(1));
        assertEquals("Marzo-2023", result.get(2));
    }

    @Test
    void generarMesesEnRango_cambioDeAnio_deberiaRetornarRangoDeMesesCorrecto() {
        // When
        List<String> result = reporteService.generarMesesEnRango(11, 2022, 2, 2023);

        // Then
        assertEquals(4, result.size());
        assertEquals("Noviembre-2022", result.get(0));
        assertEquals("Diciembre-2022", result.get(1));
        assertEquals("Enero-2023", result.get(2));
        assertEquals("Febrero-2023", result.get(3));
    }

    @Test
    void obtenerNombreMes_mesValido_deberiaRetornarNombreCorrecto() {
        // When y Then
        assertEquals("Enero", reporteService.obtenerNombreMes(1));
        assertEquals("Diciembre", reporteService.obtenerNombreMes(12));
    }

    @Test
    void obtenerNombreMes_mesInvalido_deberiaLanzarThrowException() {
        // When y Then
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.obtenerNombreMes(0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.obtenerNombreMes(13);
        });
    }

    @Test
    void validarRangoFechas_rangoFechaValido_noDeberiaLanzarThrowException() {
        // When y then
        assertDoesNotThrow(() -> {
            reporteService.validarRangoFechas(1, 2023, 3, 2023);
        });
    }

    @Test
    void validarRangoFechas_mesInvalido_deberiaLanzarThrowException() {
        // When y then
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.validarRangoFechas(0, 2023, 3, 2023);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.validarRangoFechas(1, 2023, 13, 2023);
        });
    }

    @Test
    void validarRangoFechas_ordenIncorrectoFechas_deberiaLanzarThrowException() {
        // When y Then
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.validarRangoFechas(5, 2023, 2, 2023);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.validarRangoFechas(1, 2024, 12, 2023);
        });
    }
}