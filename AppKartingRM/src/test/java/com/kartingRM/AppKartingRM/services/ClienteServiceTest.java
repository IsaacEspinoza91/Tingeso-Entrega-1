package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.repositories.ClienteRepository;
import com.kartingRM.AppKartingRM.repositories.DetalleComprobanteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ClienteServiceTest {

    private ClienteRepository clienteRepository;
    private DetalleComprobanteRepository detalleComprobanteRepository;
    private ClienteService clienteService;

    private ClienteEntity cliente;

    @BeforeEach
    void setUp() {
        clienteRepository = Mockito.mock(ClienteRepository.class);
        detalleComprobanteRepository = Mockito.mock(DetalleComprobanteRepository.class);
        clienteService = new ClienteService();

        // Inyectar mocks manualmente
        clienteService.getClass().getDeclaredFields();
        try {
            var field1 = ClienteService.class.getDeclaredField("clienteRepository");
            field1.setAccessible(true);
            field1.set(clienteService, clienteRepository);
            var field2 = ClienteService.class.getDeclaredField("detalleComprobanteRepository");
            field2.setAccessible(true);
            field2.set(clienteService, detalleComprobanteRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        cliente = new ClienteEntity();
        cliente.setId(1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setRut("12.345.678-9");
        cliente.setFechaNacimiento(LocalDate.of(1990, 4, 29));
    }

    @Test
    void getClientes_deberiaRetornarListaClientes() {
        // given
        List<ClienteEntity> lista = new ArrayList<>(List.of(cliente));
        when(clienteRepository.findAll()).thenReturn(lista);

        // when
        ArrayList<ClienteEntity> resultado = clienteService.getClientes();

        // then
        assertThat(resultado).containsExactly(cliente);
    }

    @Test
    void getClienteById_deberiaRetornarCliente() {
        // given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // when
        ClienteEntity resultado = clienteService.getClienteById(1L);

        // then
        assertThat(resultado).isEqualTo(cliente);
    }

    @Test
    void getClienteByRut_deberiaRetornarCliente() {
        // given
        when(clienteRepository.findByRut("12.345.678-9")).thenReturn(cliente);

        // when
        ClienteEntity resultado = clienteService.getClienteByRut("12.345.678-9");

        // then
        assertThat(resultado).isEqualTo(cliente);
    }

    @Test
    void findByNombreAndApellido_deberiaRetornarLista() {
        // given
        when(clienteRepository.findByNombreAndApellido("Juan", "Pérez")).thenReturn(new ArrayList<>(List.of(cliente)));

        // when
        List<ClienteEntity> resultado = clienteService.findByNombreAndApellido("Juan", "Pérez");

        // then
        assertThat(resultado).containsExactly(cliente);
    }

    @Test
    void createCliente_deberiaRetornarClienteGuardado() {
        // given
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        // when
        ClienteEntity resultado = clienteService.createCliente(cliente);

        // then
        assertThat(resultado).isEqualTo(cliente);
    }

    @Test
    void updateCliente_deberiaRetornarClienteActualizado() {
        // given
        ClienteEntity actualizado = new ClienteEntity();
        actualizado.setNombre("Carlos");
        actualizado.setApellido("Gomez");

        when(clienteRepository.save(any(ClienteEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ClienteEntity resultado = clienteService.updateCliente(1L, actualizado);

        // then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Carlos");
    }

    @Test
    void deleteCliente_deberiaRetornarTrue() throws Exception {
        // given
        doNothing().when(clienteRepository).deleteById(1L);

        // when
        boolean resultado = clienteService.deleteCliente(1L);

        // then
        assertThat(resultado).isTrue();
    }

    @Test
    void deleteCliente_deberiaLanzarExcepcion() {
        // given
        doThrow(new RuntimeException("Error")).when(clienteRepository).deleteById(1L);

        // when / then
        assertThatThrownBy(() -> clienteService.deleteCliente(1L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Error");
    }

    @Test
    void obtenerVecesUtilizadoKarting_deberiaRetornarCantidad() {
        // given
        when(clienteRepository.existsById(1L)).thenReturn(true);
        when(detalleComprobanteRepository.contarVisitasByClienteYMes(1L, 2024, 4)).thenReturn(3);

        // when
        int resultado = clienteService.obtenerVecesUtilizadoKarting(1L, 2024, 4);

        // then
        assertThat(resultado).isEqualTo(3);
    }

    @Test
    void obtenerVecesUtilizadoKarting_deberiaLanzarEntityNotFoundException() {
        // given
        when(clienteRepository.existsById(1L)).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> clienteService.obtenerVecesUtilizadoKarting(1L, 2024, 4))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    void obtenerVecesUtilizadoKarting_deberiaLanzarIllegalArgumentException() {
        // given
        when(clienteRepository.existsById(1L)).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> clienteService.obtenerVecesUtilizadoKarting(1L, 2024, 13))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mes incorrecto");
    }

    @Test
    void cumpleAnios_MismoDiaYMes_deberiaRetornarTrue() {
        // given
        LocalDate fecha = LocalDate.of(2024, 4, 29);

        // when
        boolean resultado = clienteService.cumpleAnios(cliente, fecha);

        // then
        assertThat(resultado).isTrue();
    }

    @Test
    void cumpleAnios_NoMismoDiaYMes_deberiaRetornarFalse() {
        // given
        LocalDate fecha = LocalDate.of(2024, 5, 1);

        // when
        boolean resultado = clienteService.cumpleAnios(cliente, fecha);

        // then
        assertThat(resultado).isFalse();
    }
}
