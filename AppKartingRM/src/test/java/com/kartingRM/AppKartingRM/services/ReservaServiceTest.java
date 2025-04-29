package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.*;
import com.kartingRM.AppKartingRM.repositories.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PlanService planService;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ReservaService reservaService;

    private ReservaEntity reserva;
    private PlanEntity plan;
    private ClienteEntity cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar datos de prueba
        plan = new PlanEntity();
        plan.setIdPlan(1L);
        plan.setDescripcion("Plan Básico");
        plan.setDuracionTotal(60); // 60 minutos de duración
        plan.setPrecioRegular(10000);
        plan.setPrecioFinSemana(12000);
        plan.setPrecioFeriado(15000);

        cliente = new ClienteEntity();
        cliente.setId(1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");

        reserva = new ReservaEntity();
        reserva.setIdReserva(1L);
        reserva.setFecha(LocalDate.now().plusDays(1));
        reserva.setHoraInicio(LocalTime.of(15, 0));
        reserva.setHoraFin(LocalTime.of(16, 0));
        reserva.setEstado("confirmada");
        reserva.setTotalPersonas(4);
        reserva.setPlan(plan);
        reserva.setReservante(cliente);
        reserva.setIntegrantes(new ArrayList<>());
    }

    @Test
    void getReservas_deberiaRetornarListaReservas() {
        // Given
        when(reservaRepository.findAll()).thenReturn(Collections.singletonList(reserva));

        // When
        List<ReservaEntity> result = reservaService.getReservas();

        // Then
        assertEquals(1, result.size());
        assertEquals(reserva, result.get(0));
        verify(reservaRepository).findAll();
    }

    @Test
    void getReservaById_existeReserva_deberiaRetornarReserva() {
        // Given
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // When
        ReservaEntity result = reservaService.getReservaById(1L);

        // Then
        assertNotNull(result);
        assertEquals(reserva, result);
        verify(reservaRepository).findById(1L);
    }

    @Test
    void getReservaById_noExisteReserva_deberiaLanzaThrowException() {
        // Given
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        // When y then
        assertThrows(NoSuchElementException.class, () -> {
            reservaService.getReservaById(99L);
        });
    }

    @Test
    void getReservasByClienteId_deberiaRetornarReservasDeCliente() {
        // Given
        when(reservaRepository.findByReservanteId(1L)).thenReturn(Collections.singletonList(reserva));

        // When
        List<ReservaEntity> result = reservaService.getReservasByClienteId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(reserva, result.get(0));
        verify(reservaRepository).findByReservanteId(1L);
    }

    @Test
    void createReserva_tiempoValidoSemana_deberiaReservaGuardada() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().with(DayOfWeek.TUESDAY)); // Martes
        newReserva.setHoraInicio(LocalTime.of(15, 0));
        newReserva.setTotalPersonas(3);

        when(planService.getPlanById(1L)).thenReturn(plan);
        when(clienteService.getClienteById(1L)).thenReturn(cliente);
        when(reservaRepository.findReservasExistentesEnTiempo(any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaEntity result = reservaService.createReserva(newReserva, 1L, 1L, false);

        // Then
        assertNotNull(result);
        assertEquals(cliente, result.getReservante());
        assertEquals(plan, result.getPlan());
        assertEquals(LocalTime.of(16, 0), result.getHoraFin());
        verify(reservaRepository).save(any(ReservaEntity.class));
    }

    @Test
    void createReserva_tiempoValidoSemanaFeriado_deberiaReservaGuardada() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().with(DayOfWeek.TUESDAY)); // Martes
        newReserva.setHoraInicio(LocalTime.of(15, 0));
        newReserva.setTotalPersonas(3);

        when(planService.getPlanById(1L)).thenReturn(plan);
        when(clienteService.getClienteById(1L)).thenReturn(cliente);
        when(reservaRepository.findReservasExistentesEnTiempo(any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaEntity result = reservaService.createReserva(newReserva, 1L, 1L, true);

        // Then
        assertNotNull(result);
        assertEquals(cliente, result.getReservante());
        assertEquals(plan, result.getPlan());
        assertEquals(LocalTime.of(16, 0), result.getHoraFin());
        verify(reservaRepository).save(any(ReservaEntity.class));
    }

    @Test
    void createReserva_tiempoValidoFinSemana_deberiaReservaGuardada() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().with(DayOfWeek.SATURDAY)); // Sábado
        newReserva.setHoraInicio(LocalTime.of(11, 0));
        newReserva.setTotalPersonas(3);

        when(planService.getPlanById(1L)).thenReturn(plan);
        when(clienteService.getClienteById(1L)).thenReturn(cliente);
        when(reservaRepository.findReservasExistentesEnTiempo(any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaEntity result = reservaService.createReserva(newReserva, 1L, 1L, false);

        // Then
        assertNotNull(result);
        assertEquals(LocalTime.of(12, 0), result.getHoraFin());
    }

    @Test
    void createReserva_tiemposInvalidos_deberiaLanzarThrowException() {
        // Given
        ReservaEntity existingReserva = new ReservaEntity();
        existingReserva.setIdReserva(2L);
        existingReserva.setFecha(reserva.getFecha());
        existingReserva.setHoraInicio(LocalTime.of(15, 30));
        existingReserva.setHoraFin(LocalTime.of(16, 30));

        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(reserva.getFecha());
        newReserva.setHoraInicio(LocalTime.of(15, 0));
        newReserva.setTotalPersonas(2);

        when(planService.getPlanById(1L)).thenReturn(plan);
        when(clienteService.getClienteById(1L)).thenReturn(cliente);
        when(reservaRepository.findReservasExistentesEnTiempo(any(), any(), any()))
                .thenReturn(Collections.singletonList(existingReserva));

        // When y then
        assertThrows(IllegalStateException.class, () -> {
            reservaService.createReserva(newReserva, 1L, 1L, false);
        });
    }

    @Test
    void createReserva_tiempoInicioInvalidoSemana_deberiaLanzarThrowException() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().with(DayOfWeek.MONDAY)); // Lunes
        newReserva.setHoraInicio(LocalTime.of(13, 0)); // Antes de las 14:00
        newReserva.setTotalPersonas(2);

        when(planService.getPlanById(1L)).thenReturn(plan);
        when(clienteService.getClienteById(1L)).thenReturn(cliente);
        when(reservaRepository.findReservasExistentesEnTiempo(any(), any(), any())).thenReturn(Collections.emptyList());

        // When y then
        assertThrows(IllegalStateException.class, () -> {
            reservaService.createReserva(newReserva, 1L, 1L, false);
        });
    }

    @Test
    void createReserva_tiempoFinalInvalidoSemana_deberiaThrowException() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().with(DayOfWeek.MONDAY)); // Lunes
        newReserva.setHoraInicio(LocalTime.of(22, 10));
        newReserva.setHoraFin(LocalTime.of(22, 30)); // Despues de hora de termino Karting 22:00

        when(planService.getPlanById(1L)).thenReturn(plan);
        when(clienteService.getClienteById(1L)).thenReturn(cliente);
        when(reservaRepository.findReservasExistentesEnTiempo(any(), any(), any())).thenReturn(Collections.emptyList());

        // When y then
        assertThrows(IllegalStateException.class, () -> {
            reservaService.createReserva(newReserva, 1L, 1L, false);
        });
    }

    @Test
    void createReserva_tiempoClientePlanTodosInvalidos_deberiaLanzarThrowException() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().with(DayOfWeek.MONDAY)); // Lunes

        when(planService.getPlanById(1L)).thenReturn(plan);
        when(clienteService.getClienteById(1L)).thenReturn(cliente);
        when(reservaRepository.findReservasExistentesEnTiempo(any(), any(), any())).thenReturn(Collections.emptyList());

        // When y then
        assertThrows(RuntimeException.class, () -> {
            reservaService.createReserva(newReserva, 1L, 1L, false);
        });
    }

    @Test
    void createReserva_tiempoInvalidoFinSemana_deberiaLanzarThrowException() {
        // Given
        ReservaEntity newReserva = new ReservaEntity();
        newReserva.setFecha(LocalDate.now().with(DayOfWeek.SUNDAY)); // Domingo
        newReserva.setHoraInicio(LocalTime.of(9, 0)); // Antes de las 10:00
        newReserva.setTotalPersonas(2);

        when(planService.getPlanById(1L)).thenReturn(plan);
        when(clienteService.getClienteById(1L)).thenReturn(cliente);
        when(reservaRepository.findReservasExistentesEnTiempo(any(), any(), any())).thenReturn(Collections.emptyList());

        // When y then
        assertThrows(IllegalStateException.class, () -> {
            reservaService.createReserva(newReserva, 1L, 1L, false);
        });
    }

    @Test
    void updateReserva_deberiaActualizarTiemposDeReserva() {
        // Given
        ReservaEntity updatedData = new ReservaEntity();
        updatedData.setHoraInicio(LocalTime.of(16, 0));
        updatedData.setEstado("modificada");
        updatedData.setTotalPersonas(5);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaEntity result = reservaService.updateReserva(1L, updatedData);

        // Then
        assertEquals(1L, result.getIdReserva());
        assertEquals(LocalTime.of(16, 0), result.getHoraInicio());
        assertEquals(LocalTime.of(17, 0), result.getHoraFin());
        assertEquals("modificada", result.getEstado());
        assertEquals(5, result.getTotalPersonas());
    }

    @Test
    void updateClienteDeReserva_deberiaRetornarReservanteActualizado() {
        // Given
        ClienteEntity nuevoCliente = new ClienteEntity();
        nuevoCliente.setId(2L);
        nuevoCliente.setNombre("Ana");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(clienteService.getClienteById(2L)).thenReturn(nuevoCliente);
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaEntity result = reservaService.updateClienteDeReserva(1L, 2L);

        // Then
        assertEquals(nuevoCliente, result.getReservante());
        assertEquals(LocalTime.of(16, 0), result.getHoraFin());
    }

    @Test
    void updatePlanDeReserva_deberiarRetornarReservaConPlanActualizadoYTiempoActualizado() {
        // Given
        PlanEntity nuevoPlan = new PlanEntity();
        nuevoPlan.setIdPlan(2L);
        nuevoPlan.setDuracionTotal(90); // 90 minutos

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(planService.getPlanById(2L)).thenReturn(nuevoPlan);
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaEntity result = reservaService.updatePlanDeReserva(1L, 2L);

        // Then
        assertEquals(nuevoPlan, result.getPlan());
        assertEquals(LocalTime.of(16, 30), result.getHoraFin());
    }

    @Test
    void deleteReserva_existeReserva_deberiaRetornarTrue() throws Exception {
        // Given
        when(reservaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reservaRepository).deleteById(1L);

        // When
        boolean result = reservaService.deleteReserva(1L);

        // Then
        assertTrue(result);
        verify(reservaRepository).deleteById(1L);
    }

    @Test
    void deleteReserva_noExisteReserva_deberiaLanzarThrowException() {
        // Given
        when(reservaRepository.existsById(99L)).thenReturn(false);

        // When y then
        assertThrows(Exception.class, () -> {
            reservaService.deleteReserva(99L);
        });
    }

    @Test
    void agregarIntegrante_noSeCumpleMaximoGrupoTodavia_deberiaAgregarIntegrante() {
        // Given
        ClienteEntity integrante = new ClienteEntity();
        integrante.setId(2L);
        reserva.setTotalPersonas(2); // Capacidad para 2 integrantes

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(clienteService.getClienteById(2L)).thenReturn(integrante);
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaEntity result = reservaService.agregarIntegrante(1L, 2L);

        // Then
        assertEquals(1, result.getIntegrantes().size());
        assertTrue(result.getIntegrantes().contains(integrante));
    }

    @Test
    void agregarIntegrante_seCumpleMaximoGrupo_deberiaLanzarThrowException() {
        // Given
        ClienteEntity integranteExistente = new ClienteEntity();
        integranteExistente.setId(2L);
        reserva.setTotalPersonas(1); // Capacidad para 1 integrante
        reserva.getIntegrantes().add(integranteExistente);

        ClienteEntity nuevoIntegrante = new ClienteEntity();
        nuevoIntegrante.setId(3L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(clienteService.getClienteById(3L)).thenReturn(nuevoIntegrante);

        // When y then
        assertThrows(IllegalStateException.class, () -> {
            reservaService.agregarIntegrante(1L, 3L);
        });
    }

    @Test
    void agregarIntegrante_noSeCumpleCapacidad_noDeberiaAgregarIntegrante() {
        // Given
        ClienteEntity integrante = new ClienteEntity();
        integrante.setId(1L);
        ClienteEntity integrante2 = new ClienteEntity();
        integrante2.setId(2L);
        reserva.setTotalPersonas(2);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(clienteService.getClienteById(1L)).thenReturn(integrante);
        when(clienteService.getClienteById(2L)).thenReturn(integrante);
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        reservaService.agregarIntegrante(1L, 1L);
        ReservaEntity result = reservaService.agregarIntegrante(1L, 2L);

        // Then
        assertEquals(1, result.getIntegrantes().size());
        assertTrue(result.getIntegrantes().contains(integrante));
    }

    @Test
    void quitarIntegrante_deberiaQuitarIntegrante() {
        // Given
        ClienteEntity integrante = new ClienteEntity();
        integrante.setId(2L);
        reserva.getIntegrantes().add(integrante);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(clienteService.getClienteById(2L)).thenReturn(integrante);
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaEntity result = reservaService.quitarIntegrante(1L, 2L);

        // Then
        assertTrue(result.getIntegrantes().isEmpty());
    }

    @Test
    void getIntegrantesById_existeReserva_deberiaRetornarIntegrantes() {
        // Given
        Long reservaId = 1L;
        ClienteEntity integrante1 = new ClienteEntity(2L, "9876543-2", "Ana", "López", "ana@mail.com", "987654321", LocalDate.of(2002, 5, 10), Collections.emptyList(), Collections.emptyList());
        ClienteEntity integrante2 = new ClienteEntity(3L, "11223344-5", "Carlos", "González", "carlos@mail.com", "112233445", LocalDate.of(1998, 11, 20), Collections.emptyList(), Collections.emptyList());
        reserva.setIntegrantes(Arrays.asList(integrante1, integrante2));
        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));

        // When
        List<ClienteEntity> result = reservaService.getIntegrantesById(reservaId);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(integrante1));
        assertTrue(result.contains(integrante2));
        verify(reservaRepository).findById(reservaId);
    }

    @Test
    void getIntegrantesById_noExisteReserva_deberiaLanzarThrowNoSuchElementException() {
        // Given
        Long reservaId = 2L;
        when(reservaRepository.findById(reservaId)).thenReturn(Optional.empty());

        // Wheny y then
        assertThrows(java.util.NoSuchElementException.class, () -> reservaService.getIntegrantesById(reservaId));
        verify(reservaRepository).findById(reservaId);
    }

    @Test
    void getIntegrantesById_reservaSinIntegrantes_deberiaRetornarListaVacia() {
        // Given
        Long reservaId = 1L;
        reserva.setIntegrantes(Collections.emptyList());
        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));

        // When
        List<ClienteEntity> result = reservaService.getIntegrantesById(reservaId);

        // Then
        assertTrue(result.isEmpty());
        verify(reservaRepository).findById(reservaId);
    }
}