package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.*;
import com.kartingRM.AppKartingRM.repositories.ReservaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({RackReservaService.class, ClienteService.class})
class RackReservaServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private RackReservaService rackReservaService;

    @Autowired
    private ClienteService clienteService;

    private ClienteEntity cliente;
    private PlanEntity plan;
    private ReservaEntity reservaConfirmada;
    private ReservaEntity reservaNoConfirmada;

    @BeforeEach
    void setUp() {
        // Configuración inicial común para todas las pruebas
        cliente = new ClienteEntity();
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setRut("12.345.678-9");
        entityManager.persist(cliente);

        plan = new PlanEntity();
        plan.setDescripcion("Plan Básico");
        plan.setDuracionTotal(60);
        plan.setPrecioRegular(10000);
        entityManager.persist(plan);

        // Reserva confirmada en la semana actual
        reservaConfirmada = new ReservaEntity();
        reservaConfirmada.setReservante(cliente);
        reservaConfirmada.setPlan(plan);
        reservaConfirmada.setFecha(LocalDate.now().with(DayOfWeek.MONDAY));
        reservaConfirmada.setHoraInicio(LocalTime.of(15, 0));
        reservaConfirmada.setHoraFin(LocalTime.of(16, 0));
        reservaConfirmada.setEstado("confirmada");
        entityManager.persist(reservaConfirmada);

        // Reserva no confirmada (no debería aparecer en los resultados)
        reservaNoConfirmada = new ReservaEntity();
        reservaNoConfirmada.setReservante(cliente);
        reservaNoConfirmada.setPlan(plan);
        reservaNoConfirmada.setFecha(LocalDate.now().with(DayOfWeek.TUESDAY));
        reservaNoConfirmada.setHoraInicio(LocalTime.of(16, 0));
        reservaNoConfirmada.setHoraFin(LocalTime.of(17, 0));
        reservaNoConfirmada.setEstado("pendiente");
        entityManager.persist(reservaNoConfirmada);

        entityManager.flush();
    }

    @Test
    void obtenerRackSemanal_SemanaActual_deberiaRetornarReservasConfirmadas() {
        // Given
        int semanaOffset = 0; // Semana actual

        // When
        RackSemanalDTO rackSemanal = rackReservaService.obtenerRackSemanal(semanaOffset);

        // Then
        assertNotNull(rackSemanal);
        assertEquals(7, rackSemanal.getReservasPorDia().size());

        // Verificar que la reserva confirmada está en el día correcto
        String diaReserva = reservaConfirmada.getFecha()
                .getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es", "ES"));

        List<ReservaRackDTO> reservasDia = rackSemanal.getReservasPorDia().get(diaReserva);
        assertFalse(reservasDia.isEmpty());
        assertEquals(1, reservasDia.size());
        assertEquals(reservaConfirmada.getIdReserva().toString(), reservasDia.get(0).getCodigoReserva());
    }

    @Test
    void obtenerRackSemanal_SemanaSiguiente_deberiaRetornarListaVacia() {
        // Given
        int semanaOffset = 1; // Semana siguiente

        // When
        RackSemanalDTO rackSemanal = rackReservaService.obtenerRackSemanal(semanaOffset);

        // Then
        assertNotNull(rackSemanal);

        // Verificar que todos los días están vacíos
        rackSemanal.getReservasPorDia().values().forEach(reservas -> {
            assertTrue(reservas.isEmpty());
        });
    }

    @Test
    void obtenerRackSemanal_VerificaEstructuraDias() {
        // When
        RackSemanalDTO rackSemanal = rackReservaService.obtenerRackSemanal(0);

        // Then
        Map<String, List<ReservaRackDTO>> reservasPorDia = rackSemanal.getReservasPorDia();

        // Verificar que contiene todos los días de la semana en español
        assertTrue(reservasPorDia.containsKey("lunes"));
        assertTrue(reservasPorDia.containsKey("martes"));
        assertTrue(reservasPorDia.containsKey("miércoles"));
        assertTrue(reservasPorDia.containsKey("jueves"));
        assertTrue(reservasPorDia.containsKey("viernes"));
        assertTrue(reservasPorDia.containsKey("sábado"));
        assertTrue(reservasPorDia.containsKey("domingo"));
    }

    @Test
    void obtenerRackSemanal_VerificaFormatoNombres() {
        // When
        RackSemanalDTO rackSemanal = rackReservaService.obtenerRackSemanal(0);

        // Then
        String diaReserva = reservaConfirmada.getFecha()
                .getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es", "ES"));

        List<ReservaRackDTO> reservasDia = rackSemanal.getReservasPorDia().get(diaReserva);
        ReservaRackDTO reservaDTO = reservasDia.get(0);

        assertEquals(cliente.getNombre() + " " + cliente.getApellido(), reservaDTO.getNombreReservante());
        assertEquals(reservaConfirmada.getHoraInicio(), reservaDTO.getHoraInicio());
        assertEquals(reservaConfirmada.getHoraFin(), reservaDTO.getHoraFin());
    }

    @Test
    void obtenerRackSemanal_NoIncluyeReservasNoConfirmadas_deberiaRetornarListaVacia() {
        // Para que las reservas sean consideradas en el rack, deben estar confirmadas

        // Given
        String diaReservaNoConfirmada = reservaNoConfirmada.getFecha()
                .getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es", "ES"));

        // When
        RackSemanalDTO rackSemanal = rackReservaService.obtenerRackSemanal(0);

        // Then
        List<ReservaRackDTO> reservasDia = rackSemanal.getReservasPorDia().get(diaReservaNoConfirmada);
        assertTrue(reservasDia.isEmpty());
    }
}