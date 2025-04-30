package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.*;
import com.kartingRM.AppKartingRM.repositories.ComprobanteRepository;
import com.kartingRM.AppKartingRM.repositories.DetalleComprobanteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComprobanteServiceTest {

    private static final double IVA = 0.19;

    @Mock
    private ComprobanteRepository comprobanteRepository;

    @Mock
    private DetalleComprobanteRepository detalleComprobanteRepository;

    @Mock
    private ReservaService reservaService;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ComprobanteService comprobanteService;

    private ComprobanteEntity comprobante;
    private DetalleComprobanteEntity detalle;
    private ReservaEntity reserva;
    private PlanEntity plan;
    private ClienteEntity cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar datos de prueba
        plan = new PlanEntity();
        plan.setIdPlan(1L);
        plan.setPrecioRegular(10000);
        plan.setPrecioFinSemana(12000);
        plan.setPrecioFeriado(15000);
        plan.setDuracionTotal(60);

        cliente = new ClienteEntity();
        cliente.setId(1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setFechaNacimiento(LocalDate.of(1990, Month.JANUARY, 1));

        ClienteEntity cliente2 = new ClienteEntity();
        cliente2.setId(2L);
        cliente2.setNombre("Ana");
        cliente2.setApellido("López");
        cliente2.setFechaNacimiento(LocalDate.of(1995, Month.MARCH, 15));

        ClienteEntity cliente3 = new ClienteEntity();
        cliente3.setId(3L);
        cliente3.setNombre("Carlos");
        cliente3.setApellido("González");
        cliente3.setFechaNacimiento(LocalDate.of(2000, Month.JULY, 20));

        ClienteEntity cliente4 = new ClienteEntity();
        cliente4.setId(4L);
        cliente4.setNombre("Sofía");
        cliente4.setApellido("Martínez");
        cliente4.setFechaNacimiento(LocalDate.of(2003, Month.DECEMBER, 5));

        reserva = new ReservaEntity();
        reserva.setIdReserva(1L);
        reserva.setFecha(LocalDate.now());
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(11, 0));
        reserva.setEstado("confirmada");
        reserva.setTotalPersonas(4);
        reserva.setPlan(plan);
        reserva.setReservante(cliente);
        reserva.setIntegrantes(Arrays.asList(cliente, cliente2, cliente3, cliente4)); // Añade los 4 clientes


        comprobante = new ComprobanteEntity();
        comprobante.setIdComprobante(1L);
        comprobante.setPagado(true);
        comprobante.setTotal(10000);
        comprobante.setReserva(reserva);

        detalle = new DetalleComprobanteEntity();
        detalle.setIdDetalle(1L);
        detalle.setComprobante(comprobante);
        detalle.setCliente(cliente);
        detalle.setTarifa(10000);
        detalle.setDescuentoGrupo(1000);
        detalle.setDescuentoEspecial(500);
        detalle.setDescuentoExtra(0);
        detalle.setMontoFinal(8500);
        detalle.setMontoIva(1615);
        detalle.setMontoTotal(10115);
    }

    @Test
    void crearComprobanteDesdeReserva_infoValida_deberiaRetornarComprobanteGuardado() {
        // Given
        when(reservaService.getReservaById(1L)).thenReturn(reserva);
        when(reservaService.getIntegrantesById(1L)).thenReturn(reserva.getIntegrantes());
        when(clienteService.cumpleAnios(any(), any())).thenReturn(false);
        when(clienteService.obtenerVecesUtilizadoKarting(anyLong(), anyInt(), anyInt())).thenReturn(0);

        // Configura el mock para la primera llamada a save (cuando se crea el comprobante inicial)
        when(comprobanteRepository.save(any(ComprobanteEntity.class))).thenReturn(comprobante);

        when(detalleComprobanteRepository.save(any())).thenReturn(detalle);

        // Configura el mock para la llamada a findById dentro de actualizarTotalComprobante
        when(comprobanteRepository.findById(anyLong())).thenReturn(Optional.of(comprobante));

        // When
        ComprobanteEntity result = comprobanteService.crearComprobanteDesdeReserva(1L, false, 0);

        // Then
        assertNotNull(result);
        assertEquals(reserva, result.getReserva());
        verify(comprobanteRepository).findById(comprobante.getIdComprobante()); // Verificamos llamada a actualizarTotalComprobante
    }

    @Test
    void crearComprobanteDesdeReserva_infoValida_cumpleanios_deberiaRetornarComprobanteGuardado() {
        // Given
        // Modificar la fecha de la reserva
        reserva.setFecha(LocalDate.of(2025, Month.JANUARY, 1));

        // Modificar la fecha de nacimiento del cliente
        cliente.setFechaNacimiento(LocalDate.of(2000, Month.JANUARY, 1));

        when(reservaService.getReservaById(1L)).thenReturn(reserva);
        when(reservaService.getIntegrantesById(1L)).thenReturn(reserva.getIntegrantes());
        when(clienteService.cumpleAnios(cliente, reserva.getFecha())).thenReturn(true); // Ahora debería cumplir años
        when(clienteService.obtenerVecesUtilizadoKarting(anyLong(), anyInt(), anyInt())).thenReturn(0);

        // Configura el mock para la primera llamada a save (cuando se crea el comprobante inicial)
        when(comprobanteRepository.save(any(ComprobanteEntity.class))).thenReturn(comprobante);

        // Capturar el DetalleComprobante para verificar el descuento de cumpleaños
        ArgumentCaptor<DetalleComprobanteEntity> detalleCaptor = ArgumentCaptor.forClass(DetalleComprobanteEntity.class);
        when(detalleComprobanteRepository.save(detalleCaptor.capture())).thenReturn(detalle);

        // Configura el mock para la llamada a findById dentro de actualizarTotalComprobante
        when(comprobanteRepository.findById(anyLong())).thenReturn(Optional.of(comprobante));

        // When
        ComprobanteEntity result = comprobanteService.crearComprobanteDesdeReserva(1L, false, 0);

        // Then
        assertNotNull(result);
        assertEquals(reserva, result.getReserva());
        verify(comprobanteRepository).findById(comprobante.getIdComprobante()); // Verificamos llamada a actualizarTotalComprobante
        verify(detalleComprobanteRepository, times(reserva.getTotalPersonas())).save(any());

        // Verificar que al menos un detalle tenga descuento de cumpleaños
        boolean tieneDescuentoCumpleanios = false;
        for (DetalleComprobanteEntity detalleGuardado : detalleCaptor.getAllValues()) {
            if (detalleGuardado.isTieneDescuentoCumpleanios()) {
                tieneDescuentoCumpleanios = true;
                break;
            }
        }
        assertTrue(tieneDescuentoCumpleanios, "Al menos un detalle debería tener descuento de cumpleaños");
    }

    @Test
    void crearComprobanteDesdeReserva_descuentoNegativo_deberiaRetornarNull() {
        // When
        ComprobanteEntity result = comprobanteService.crearComprobanteDesdeReserva(1L, false, -100);

        // Then
        assertNull(result);
    }

    @Test
    void crearComprobanteDesdeReserva_sinIntegrantes_deberiaLanzarThrowException() {
        // Given
        reserva.setIntegrantes(Collections.emptyList());
        when(reservaService.getReservaById(1L)).thenReturn(reserva);

        // When y then
        assertThrows(IllegalStateException.class, () -> {
            comprobanteService.crearComprobanteDesdeReserva(1L, false, 0);
        });
    }

    @Test
    void crearComprobanteDesdeReserva_sinTodosLosIntegrantes_deberiaLanzarThrowException() {
        // Given
        reserva.setTotalPersonas(2); // Solo 1 integrante en la lista
        when(reservaService.getReservaById(1L)).thenReturn(reserva);
        when(reservaService.getIntegrantesById(1L)).thenReturn(reserva.getIntegrantes());

        // When y then
        assertThrows(IllegalStateException.class, () -> {
            comprobanteService.crearComprobanteDesdeReserva(1L, false, 0);
        });
    }

    @Test
    void calcularTarifaBase_semana_deberiaRetornarPrecioSemana() {
        // Given
        reserva.setFecha(LocalDate.now().with(Month.JANUARY).withDayOfMonth(2)); // Martes

        // When
        int result = comprobanteService.calcularTarifaBase(reserva, false);

        // Then
        assertEquals(plan.getPrecioRegular(), result);
    }

    @Test
    void calcularTarifaBase_finSemana_deberiaRetornarPrecioFinSemana() {
        // Given
        reserva.setFecha(LocalDate.now().with(Month.JANUARY)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY))); // Dia sabado de enero. Fin de semana

        // When
        int result = comprobanteService.calcularTarifaBase(reserva, false);

        // Then
        assertEquals(plan.getPrecioFinSemana(), result);
    }

    @Test
    void calcularTarifaBase_feriado_deberiaRetornarPrecioFeriado() {
        // When
        int result = comprobanteService.calcularTarifaBase(reserva, true);

        // Then
        assertEquals(plan.getPrecioFeriado(), result);
    }

    @Test
    void calcularDescuentoGrupo_diferentesTamanios_deberiaRetornarDescuentoCorrecto() {
        assertEquals(0.0, comprobanteService.calcularDescuentoGrupo(1));
        assertEquals(0.0, comprobanteService.calcularDescuentoGrupo(2));
        assertEquals(0.1, comprobanteService.calcularDescuentoGrupo(3));
        assertEquals(0.1, comprobanteService.calcularDescuentoGrupo(5));
        assertEquals(0.2, comprobanteService.calcularDescuentoGrupo(6));
        assertEquals(0.2, comprobanteService.calcularDescuentoGrupo(10));
        assertEquals(0.3, comprobanteService.calcularDescuentoGrupo(11));
        assertEquals(0.3, comprobanteService.calcularDescuentoGrupo(15));
    }

    @Test
    void calcularDescuentoCumpleanios_cumpleanios_deberiaRetornar50Porciento() {
        // Given
        LocalDate fechaReserva = LocalDate.of(2023, Month.JANUARY, 1);
        when(clienteService.cumpleAnios(cliente, fechaReserva)).thenReturn(true);

        // When
        double result = comprobanteService.calcularDescuentoCumpleanios(cliente, fechaReserva);

        // Then
        assertEquals(0.5, result);
    }

    @Test
    void calcularDescuentoCumpleanios_noCumpleanios_deberiaRetornar0Porciento() {
        // Given
        LocalDate fechaReserva = LocalDate.of(2023, Month.JANUARY, 1);
        when(clienteService.cumpleAnios(cliente, fechaReserva)).thenReturn(true);

        // When
        double result = comprobanteService.calcularDescuentoCumpleanios(cliente, null);

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void calcularDescuentoFrecuente_variasVisitas_deberiaRetornarDescuentoCorrecto() {
        // Given
        LocalDate fecha = LocalDate.now();
        when(clienteService.obtenerVecesUtilizadoKarting(1L, fecha.getYear(), fecha.getMonthValue()))
                .thenReturn(0).thenReturn(1).thenReturn(2).thenReturn(4).thenReturn(5).thenReturn(6).thenReturn(7);

        // When y then
        assertEquals(0.0, comprobanteService.calcularDescuentoFrecuente(cliente, fecha));
        assertEquals(0.0, comprobanteService.calcularDescuentoFrecuente(cliente, fecha));
        assertEquals(0.1, comprobanteService.calcularDescuentoFrecuente(cliente, fecha));
        assertEquals(0.1, comprobanteService.calcularDescuentoFrecuente(cliente, fecha));
        assertEquals(0.2, comprobanteService.calcularDescuentoFrecuente(cliente, fecha));
        assertEquals(0.2, comprobanteService.calcularDescuentoFrecuente(cliente, fecha));
        assertEquals(0.3, comprobanteService.calcularDescuentoFrecuente(cliente, fecha));
    }

    @Test
    void getComprobantes_deberiaRetornarListaComprobantes() {
        // Given
        ComprobanteEntity comprobante2 = new ComprobanteEntity();
        comprobante2.setIdComprobante(2L);
        comprobante2.setPagado(false);
        comprobante2.setTotal(25000);
        comprobante2.setReserva(reserva); // Reutilizamos la reserva existente para este ejemplo

        List<ComprobanteEntity> listaComprobantes = Arrays.asList(comprobante, comprobante2);
        when(comprobanteRepository.findAll()).thenReturn(listaComprobantes);

        // When
        List<ComprobanteEntity> result = comprobanteService.getComprobantes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(comprobante));
        assertTrue(result.contains(comprobante2));
        verify(comprobanteRepository).findAll();
    }

    @Test
    void actualizarTotalComprobante_deberiaCalcularTotalDeComprobante() {
        // Given
        comprobante.getDetalles().add(detalle);
        when(comprobanteRepository.findById(1L)).thenReturn(Optional.of(comprobante));

        // When
        comprobanteService.actualizarTotalComprobante(1L);

        // Then
        assertEquals(detalle.getMontoTotal(), comprobante.getTotal());
        verify(comprobanteRepository).save(comprobante);
    }

    @Test
    void createComprobante_reservaValiad_deberiaLanzarCreateComprobante() {
        // Given
        when(reservaService.getReservaById(1L)).thenReturn(reserva);
        when(comprobanteRepository.save(any())).thenReturn(comprobante);

        // When
        ComprobanteEntity result = comprobanteService.createComprobante(new ComprobanteEntity(), 1L);

        // Then
        assertNotNull(result);
        assertEquals(reserva, result.getReserva());
        verify(comprobanteRepository).save(any());
    }

    @Test
    void createComprobante_resservaInvalida_deberiaLanzarThrowException() {
        // Given
        when(reservaService.getReservaById(1L)).thenReturn(null);

        // When y then
        assertThrows(RuntimeException.class, () -> {
            comprobanteService.createComprobante(new ComprobanteEntity(), 1L);
        });
    }

    @Test
    void updateComprobante_deberiaRetornarComprobanteActualizado() {
        // Given
        ComprobanteEntity updated = new ComprobanteEntity();
        updated.setPagado(false);

        when(comprobanteRepository.findById(1L)).thenReturn(Optional.of(comprobante));
        when(comprobanteRepository.save(any())).thenAnswer(invocation -> {
            ComprobanteEntity saved = invocation.getArgument(0);
            saved.setIdComprobante(1L);
            saved.setReserva(comprobante.getReserva());
            return saved;
        });

        // When
        ComprobanteEntity result = comprobanteService.updateComprobante(1L, updated);

        // Then
        assertFalse(result.isPagado());
        assertEquals(reserva, result.getReserva());
    }

    @Test
    void updateReservaDeComprobante_deberiaRetornarReservaActualizado() {
        // Given
        ReservaEntity nuevaReserva = new ReservaEntity();
        nuevaReserva.setIdReserva(2L);

        when(comprobanteRepository.findById(1L)).thenReturn(Optional.of(comprobante));
        when(reservaService.getReservaById(2L)).thenReturn(nuevaReserva);
        when(comprobanteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ComprobanteEntity result = comprobanteService.updateReservaDeComprobante(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals(nuevaReserva, result.getReserva());
        verify(comprobanteRepository).findById(1L);
        verify(reservaService).getReservaById(2L);
        verify(comprobanteRepository).save(comprobante); // Verifica que se haya llamado a save con el comprobante modificado
    }

    @Test
    void updateClienteDeDetalle_deberiaRetornarClienteActualizado() {
        // Given
        ClienteEntity nuevoCliente = new ClienteEntity();
        nuevoCliente.setId(2L);
        nuevoCliente.setNombre("Ana");
        nuevoCliente.setApellido("López");

        when(detalleComprobanteRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(clienteService.getClienteById(2L)).thenReturn(nuevoCliente);
        when(detalleComprobanteRepository.save(any(DetalleComprobanteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DetalleComprobanteEntity result = comprobanteService.updateClienteDeDetalle(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals(nuevoCliente, result.getCliente());
        verify(detalleComprobanteRepository).findById(1L);
        verify(clienteService).getClienteById(2L);
        verify(detalleComprobanteRepository).save(detalle); // Verifica que se guarde el detalle modificado
    }

    @Test
    void updateClienteDeDetalle_noExisteDetalleId_deberiaLanzarThrowNoSuchElementException() {
        // Given
        when(detalleComprobanteRepository.findById(1L)).thenReturn(Optional.empty());

        // When y then
        assertThrows(NoSuchElementException.class, () -> comprobanteService.updateClienteDeDetalle(1L, 2L));
        verify(detalleComprobanteRepository).findById(1L);
        verifyNoMoreInteractions(detalleComprobanteRepository); // Verifica que NO haya más interacciones DESPUÉS de findById
        verifyNoInteractions(clienteService);
    }

    @Test
    void updateClienteDeDetalle_noExisteClienteId_deberiaLanzarThrowNoSuchElementException() {
        // Given
        when(detalleComprobanteRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(clienteService.getClienteById(2L)).thenReturn(null); // Simulate cliente not found

        // When y then
        assertThrows(NoSuchElementException.class, () -> comprobanteService.updateClienteDeDetalle(1L, 2L));
        verify(detalleComprobanteRepository).findById(1L);
        verify(clienteService).getClienteById(2L);
        verifyNoMoreInteractions(detalleComprobanteRepository); // Verifica que NO haya más interacciones DESPUÉS de findById
    }

    @Test
    void getDetalleComprobantesByClienteId_existeClienteId_deberiaRetornarDetallesDeCliente() {
        // Given
        DetalleComprobanteEntity detalle2ParaCliente = new DetalleComprobanteEntity();
        detalle2ParaCliente.setIdDetalle(2L);
        detalle2ParaCliente.setTarifa(15000.0);
        detalle2ParaCliente.setComprobante(comprobante);
        detalle2ParaCliente.setCliente(cliente);

        List<DetalleComprobanteEntity> detallesDelCliente = Arrays.asList(detalle, detalle2ParaCliente);
        when(detalleComprobanteRepository.findByClienteId(1L)).thenReturn(detallesDelCliente);

        // When
        List<DetalleComprobanteEntity> result = comprobanteService.getDetalleComprobantesByClienteId(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(detalle));
        assertTrue(result.contains(detalle2ParaCliente));
        verify(detalleComprobanteRepository).findByClienteId(1L);
    }

    @Test
    void getDetalleComprobantesByClienteId_noExisteClienteId_deberiaRetornarListaVacia() {
        // Given
        when(detalleComprobanteRepository.findByClienteId(99L)).thenReturn(Collections.emptyList());

        // When
        List<DetalleComprobanteEntity> result = comprobanteService.getDetalleComprobantesByClienteId(99L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(detalleComprobanteRepository).findByClienteId(99L);
    }

    @Test
    void getDetalleComprobanteByClienteIdAndComprobanteId_existenIds_deberiaRetornarDetalle() {
        // Given
        when(detalleComprobanteRepository.findByClienteIdAndComprobanteIdComprobante(1L, 1L)).thenReturn(detalle);

        // When
        DetalleComprobanteEntity result = comprobanteService.getDetalleComprobanteByClienteIdAndComprobanteId(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(detalle, result);
        verify(detalleComprobanteRepository).findByClienteIdAndComprobanteIdComprobante(1L, 1L);
    }

    @Test
    void getDetalleComprobanteByClienteIdAndComprobanteId_noExisteClienteId_deberiaRetornarNull() {
        // Given
        when(detalleComprobanteRepository.findByClienteIdAndComprobanteIdComprobante(99L, 1L)).thenReturn(null);

        // When
        DetalleComprobanteEntity result = comprobanteService.getDetalleComprobanteByClienteIdAndComprobanteId(99L, 1L);

        // Then
        assertNull(result);
        verify(detalleComprobanteRepository).findByClienteIdAndComprobanteIdComprobante(99L, 1L);
    }

    @Test
    void getDetalleComprobanteByClienteIdAndComprobanteId_noExisteComprobanteId_deberiaRetornarNull() {
        // Given
        when(detalleComprobanteRepository.findByClienteIdAndComprobanteIdComprobante(1L, 99L)).thenReturn(null);

        // When
        DetalleComprobanteEntity result = comprobanteService.getDetalleComprobanteByClienteIdAndComprobanteId(1L, 99L);

        // Then
        assertNull(result);
        verify(detalleComprobanteRepository).findByClienteIdAndComprobanteIdComprobante(1L, 99L);
    }

    @Test
    void getDetalleComprobanteByClienteIdAndComprobanteId_noExistenIds_deberiaRetornarNull() {
        // Given
        when(detalleComprobanteRepository.findByClienteIdAndComprobanteIdComprobante(99L, 99L)).thenReturn(null);

        // When
        DetalleComprobanteEntity result = comprobanteService.getDetalleComprobanteByClienteIdAndComprobanteId(99L, 99L);

        // Then
        assertNull(result);
        verify(detalleComprobanteRepository).findByClienteIdAndComprobanteIdComprobante(99L, 99L);
    }

    @Test
    void getDetalleComprobantes_deberiaRetornarListaDetalles() {
        // Given
        DetalleComprobanteEntity detalle2 = new DetalleComprobanteEntity();
        detalle2.setIdDetalle(2L);
        detalle2.setTarifa(15000.0);
        detalle2.setComprobante(comprobante);
        detalle2.setCliente(cliente);

        List<DetalleComprobanteEntity> listaDetalles = Arrays.asList(detalle, detalle2);
        when(detalleComprobanteRepository.findAll()).thenReturn(listaDetalles);

        // When
        List<DetalleComprobanteEntity> result = comprobanteService.getDetalleComprobantes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(detalle));
        assertTrue(result.contains(detalle2));
        verify(detalleComprobanteRepository).findAll();
    }

    @Test
    void getDetallesByComprobante_existeComproabanteId_deberiaRetornarDetallesDeComprobante() {
        // Given
        DetalleComprobanteEntity detalle2ParaComprobante = new DetalleComprobanteEntity();
        detalle2ParaComprobante.setIdDetalle(2L);
        detalle2ParaComprobante.setTarifa(15000.0);
        detalle2ParaComprobante.setComprobante(comprobante);
        detalle2ParaComprobante.setCliente(cliente);

        List<DetalleComprobanteEntity> detallesDelComprobante = Arrays.asList(detalle, detalle2ParaComprobante);
        when(detalleComprobanteRepository.findByComprobanteIdComprobante(1L)).thenReturn(detallesDelComprobante);

        // When
        List<DetalleComprobanteEntity> result = comprobanteService.getDetallesByComprobante(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(detalle));
        assertTrue(result.contains(detalle2ParaComprobante));
        verify(detalleComprobanteRepository).findByComprobanteIdComprobante(1L);
    }

    @Test
    void getDetallesByComprobante_noExisteComproabanteId_deberiaRetornarListaVacia() {
        // Given
        when(detalleComprobanteRepository.findByComprobanteIdComprobante(99L)).thenReturn(Collections.emptyList());

        // When
        List<DetalleComprobanteEntity> result = comprobanteService.getDetallesByComprobante(99L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(detalleComprobanteRepository).findByComprobanteIdComprobante(99L);
    }

    @Test
    void getDetalleComprobanteById_existeId_deberiaRetornarDetalle() {
        // Given
        when(detalleComprobanteRepository.findById(1L)).thenReturn(Optional.of(detalle));

        // When
        DetalleComprobanteEntity result = comprobanteService.getDetalleComprobanteById(1L);

        // Then
        assertNotNull(result);
        assertEquals(detalle, result);
        verify(detalleComprobanteRepository).findById(1L);
    }

    @Test
    void getDetalleComprobanteById_noExisteId_deberiaLanzarThrowNoSuchElementException() {
        // Given
        when(detalleComprobanteRepository.findById(99L)).thenReturn(Optional.empty());

        // When y then
        assertThrows(NoSuchElementException.class, () -> comprobanteService.getDetalleComprobanteById(99L));
        verify(detalleComprobanteRepository).findById(99L);
    }

    @Test
    void deleteComprobante_existe_deberiaRetornarTrue() throws Exception {
        // Given
        when(comprobanteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(comprobanteRepository).deleteById(1L);

        // When
        boolean result = comprobanteService.deleteComprobante(1L);

        // Then
        assertTrue(result);
        verify(comprobanteRepository).deleteById(1L);
    }

    @Test
    void deleteComprobante_noExiste_deberiaLanzarThrowException() {
        // Given
        when(comprobanteRepository.existsById(1L)).thenReturn(false);
        // Configura el mock para que deleteById lance una excepción
        doThrow(new EmptyResultDataAccessException(1)).when(comprobanteRepository).deleteById(1L);

        // When y then
        assertThrows(Exception.class, () -> {
            comprobanteService.deleteComprobante(1L);
        });
    }

    @Test
    void createDetalleComprobante_deberiaRetornarDetalle() {
        // Given
        when(comprobanteRepository.findById(1L)).thenReturn(Optional.of(comprobante));
        when(clienteService.getClienteById(1L)).thenReturn(cliente);
        when(detalleComprobanteRepository.save(any())).thenReturn(detalle);

        // When
        DetalleComprobanteEntity result = comprobanteService.createDetalleComprobante(new DetalleComprobanteEntity(), 1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(comprobante, result.getComprobante());
        assertEquals(cliente, result.getCliente());
    }

    @Test
    void updateDetalle_deberiaActulizarDetalleConNuevoHorario() {
        // Given
        DetalleComprobanteEntity updated = new DetalleComprobanteEntity();
        updated.setTarifa(20000);
        updated.setPorcentajeDescuentoGrupo(10.0);
        updated.setPorcentajeDescuentoEspecial(5.0);
        updated.setDescuentoExtra(1000);

        when(detalleComprobanteRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(comprobanteRepository.findById(1L)).thenReturn(Optional.of(comprobante));

        // Configura el mock para que devuelva el objeto que se guarda
        when(detalleComprobanteRepository.save(any(DetalleComprobanteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DetalleComprobanteEntity result = comprobanteService.updateDetalle(1L, updated);

        // Then
        assertNotNull(result);
        assertEquals(20000.0, result.getTarifa()); // Usa el valor esperado del objeto 'updated'
        verify(comprobanteRepository).save(comprobante);
    }

    /*
    @Test
    void deleteDetalleComprobante_deberiaEliminarRelaciones() throws Exception {
        // Crear objetos reales con listas bien configuradas
        ComprobanteEntity comprobante = new ComprobanteEntity();
        DetalleComprobanteEntity detalle = new DetalleComprobanteEntity();
        ReservaEntity reserva = new ReservaEntity();
        ClienteEntity cliente = new ClienteEntity();

        // Configurar relaciones
        detalle.setIdDetalle(1L);
        detalle.setComprobante(comprobante);
        detalle.setCliente(cliente);

        comprobante.setDetalles(new ArrayList<>(List.of(detalle)));
        comprobante.setReserva(reserva);

        reserva.setIntegrantes(new ArrayList<>(List.of(cliente)));

        cliente.setReservasComoIntegrante(new ArrayList<>(List.of(reserva)));

        // Asegúrate de que el repository devuelva ese mismo detalle
        when(detalleComprobanteRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(comprobanteRepository.findById(10L)).thenReturn(Optional.of(comprobante));

        doNothing().when(detalleComprobanteRepository).deleteById(1L);

        // When
        boolean result = comprobanteService.deleteDetalleComprobante(1L);

        // Then
        assertTrue(result);
        verify(detalleComprobanteRepository).deleteById(1L);
    }*/


}