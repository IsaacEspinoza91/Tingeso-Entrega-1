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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ReservaService.class, PlanService.class, ClienteService.class})
class ReservaServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private PlanService planService;

    @Autowired
    private ClienteService clienteService;

    private ClienteEntity cliente;
    private PlanEntity plan;
    private ReservaEntity reserva;

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
        plan.setPrecioFinSemana(12000);
        plan.setPrecioFeriado(15000);
        entityManager.persist(plan);

        reserva = new ReservaEntity();
        reserva.setReservante(cliente);
        reserva.setPlan(plan);
        reserva.setFecha(LocalDate.now().plusDays(1));
        reserva.setHoraInicio(LocalTime.of(15, 0));
        reserva.setHoraFin(LocalTime.of(16, 0));
        reserva.setEstado("confirmada");
        reserva.setTotalPersonas(4);
        entityManager.persist(reserva);

        entityManager.flush();
    }

    @Test
    @DisplayName("Obtener todas las reservas")
    void getReservas_RetornaListaReservas() {
        // When
        List<ReservaEntity> reservas = reservaService.getReservas();

        // Then
        assertFalse(reservas.isEmpty());
        assertEquals(1, reservas.size());
    }

    @Test
    @DisplayName("Obtener reserva por ID - Cuando existe")
    void getReservaById_WhenExists_ReturnsReserva() {
        // When
        ReservaEntity foundReserva = reservaService.getReservaById(reserva.getIdReserva());

        // Then
        assertNotNull(foundReserva);
        assertEquals(reserva.getIdReserva(), foundReserva.getIdReserva());
    }

    @Test
    @DisplayName("Obtener reserva por ID - Cuando no existe")
    void getReservaById_WhenNotExists_ThrowsException() {
        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            reservaService.getReservaById(999L);
        });
    }

    @Test
    @DisplayName("Obtener reservas por cliente ID")
    void getReservasByClienteId_ReturnsReservasCliente() {
        // When
        List<ReservaEntity> reservas = reservaService.getReservasByClienteId(cliente.getId());

        // Then
        assertFalse(reservas.isEmpty());
        assertEquals(1, reservas.size());
        assertEquals(cliente.getId(), reservas.get(0).getReservante().getId());
    }

    @Test
    @DisplayName("Crear reserva - Horario válido día semana")
    void createReserva_HorarioValidoDiaSemana_ReturnsSavedReserva() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().plusDays(2)); // Día de semana
        newReserva.setHoraInicio(LocalTime.of(15, 0));
        newReserva.setTotalPersonas(3);

        // When
        ReservaEntity savedReserva = reservaService.createReserva(newReserva, cliente.getId(), plan.getIdPlan(), false);

        // Then
        assertNotNull(savedReserva.getIdReserva());
        assertEquals(LocalTime.of(16, 0), savedReserva.getHoraFin());
    }

    @Test
    @DisplayName("Crear reserva - Horario inválido día semana")
    void createReserva_HorarioInvalidoDiaSemana_ThrowsException() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().plusDays(2)); // Día de semana
        newReserva.setHoraInicio(LocalTime.of(13, 0)); // Fuera de horario
        newReserva.setTotalPersonas(3);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            reservaService.createReserva(newReserva, cliente.getId(), plan.getIdPlan(), false);
        });
    }

    @Test
    @DisplayName("Crear reserva - Horario válido fin de semana")
    void createReserva_HorarioValidoFinSemana_ReturnsSavedReserva() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().with(DayOfWeek.SATURDAY)); // Sábado
        newReserva.setHoraInicio(LocalTime.of(11, 0));
        newReserva.setTotalPersonas(3);

        // When
        ReservaEntity savedReserva = reservaService.createReserva(newReserva, cliente.getId(), plan.getIdPlan(), false);

        // Then
        assertNotNull(savedReserva.getIdReserva());
        assertEquals(LocalTime.of(12, 0), savedReserva.getHoraFin());
    }

    @Test
    @DisplayName("Actualizar reserva")
    void updateReserva_ReturnsUpdatedReserva() {
        // Given
        reserva.setTotalPersonas(5);

        // When
        ReservaEntity updatedReserva = reservaService.updateReserva(reserva.getIdReserva(), reserva);

        // Then
        assertEquals(5, updatedReserva.getTotalPersonas());
    }

    @Test
    @DisplayName("Eliminar reserva - Cuando existe")
    void deleteReserva_WhenExists_ReturnsTrue() throws Exception {
        // When
        boolean result = reservaService.deleteReserva(reserva.getIdReserva());

        // Then
        assertTrue(result);
        assertFalse(reservaRepository.existsById(reserva.getIdReserva()));
    }

    @Test
    @DisplayName("Agregar integrante a reserva")
    void agregarIntegrante_WhenSpaceAvailable_ReturnsUpdatedReserva() {
        // Given
        ClienteEntity nuevoIntegrante = new ClienteEntity();
        nuevoIntegrante.setNombre("Ana");
        nuevoIntegrante.setApellido("Gómez");
        nuevoIntegrante.setRut("98.765.432-1");
        entityManager.persist(nuevoIntegrante);
        entityManager.flush();

        // When
        ReservaEntity updatedReserva = reservaService.agregarIntegrante(reserva.getIdReserva(), nuevoIntegrante.getId());

        // Then
        assertTrue(updatedReserva.getIntegrantes().contains(nuevoIntegrante));
    }
}