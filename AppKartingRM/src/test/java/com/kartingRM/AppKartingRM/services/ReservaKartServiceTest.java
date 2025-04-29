package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.*;
import com.kartingRM.AppKartingRM.repositories.ReservaKartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaKartServiceTest {

    @Mock
    private ReservaKartRepository reservaKartRepository;

    @Mock
    private ReservaService reservaService;

    @Mock
    private KartService kartService;

    @InjectMocks
    private ReservaKartService reservaKartService;

    private ReservaEntity reserva;
    private KartEntity kart;
    private ReservaKartEntity reservaKart;
    private ReservaKartId reservaKartId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar datos de prueba
        reserva = new ReservaEntity();
        reserva.setIdReserva(1L);
        reserva.setFecha(LocalDate.now());
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(11, 0));
        reserva.setEstado("confirmada");

        kart = new KartEntity();
        kart.setIdkart(1L);
        kart.setModelo("Kart Pro");
        kart.setEstado("disponible");

        reservaKartId = new ReservaKartId();
        reservaKartId.setReserva(1L);
        reservaKartId.setKart(1L);

        reservaKart = new ReservaKartEntity();
        reservaKart.setReserva(reserva);
        reservaKart.setKart(kart);
    }

    @Test
    void getAllReservaKarts_deberiaRetornarListaReservaKarts() {
        // Given
        when(reservaKartRepository.findAll()).thenReturn(Collections.singletonList(reservaKart));

        // When
        List<ReservaKartEntity> result = reservaKartService.getAllReservaKarts();

        // Then
        assertEquals(1, result.size());
        assertEquals(reservaKart, result.get(0));
        verify(reservaKartRepository).findAll();
    }

    @Test
    void getKartsByReserva_deberiaRetornarKartsDeReserva() {
        // Given
        when(reservaKartRepository.findByReservaIdReserva(1L)).thenReturn(Collections.singletonList(reservaKart));

        // When
        List<ReservaKartEntity> result = reservaKartService.getKartsByReserva(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(reservaKart, result.get(0));
        verify(reservaKartRepository).findByReservaIdReserva(1L);
    }

    @Test
    void getReservasByKart_deberiaRetornarReservasDeKart() {
        // Given
        when(reservaKartRepository.findByKartIdkart(1L)).thenReturn(Collections.singletonList(reservaKart));

        // When
        List<ReservaKartEntity> result = reservaKartService.getReservasByKart(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(reservaKart, result.get(0));
        verify(reservaKartRepository).findByKartIdkart(1L);
    }

    @Test
    void createReservaKart_infoValida_deberiaRetornarReservaKartGuardada() {
        // Given
        when(reservaService.getReservaById(1L)).thenReturn(reserva);
        when(kartService.getKartById(1L)).thenReturn(kart);
        when(reservaKartRepository.existsByReservaIdReservaAndKartIdkart(1L, 1L)).thenReturn(false);
        when(reservaKartRepository.save(any(ReservaKartEntity.class))).thenReturn(reservaKart);

        // When
        ReservaKartEntity result = reservaKartService.createReservaKart(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(reserva, result.getReserva());
        assertEquals(kart, result.getKart());
        verify(reservaKartRepository).save(any(ReservaKartEntity.class));
    }

    @Test
    void createReservaKart_noExisteReserva_deberiaLanzarThrowException() {
        // Given
        when(reservaService.getReservaById(1L)).thenReturn(null);
        when(kartService.getKartById(1L)).thenReturn(kart);

        // When y Then
        assertThrows(RuntimeException.class, () -> {
            reservaKartService.createReservaKart(1L, 1L);
        });
    }

    @Test
    void createReservaKart_noExisteKart_deberiaLanzarThrowException() {
        // Given
        when(reservaService.getReservaById(1L)).thenReturn(reserva);
        when(kartService.getKartById(1L)).thenReturn(null);

        // When y Then
        assertThrows(RuntimeException.class, () -> {
            reservaKartService.createReservaKart(1L, 1L);
        });
    }

    @Test
    void createReservaKart_yaExisteRelacion_deberiaLanzarThrowException() {
        // Given
        when(reservaService.getReservaById(1L)).thenReturn(reserva);
        when(kartService.getKartById(1L)).thenReturn(kart);
        when(reservaKartRepository.existsByReservaIdReservaAndKartIdkart(1L, 1L)).thenReturn(true);

        // When y Then
        assertThrows(IllegalStateException.class, () -> {
            reservaKartService.createReservaKart(1L, 1L);
        });
    }

    @Test
    void deleteReservaKart_infoValida_deberiaEliminarReservaKart() {
        // Given
        when(reservaKartRepository.existsById(reservaKartId)).thenReturn(true);
        doNothing().when(reservaKartRepository).deleteById(reservaKartId);

        // When
        reservaKartService.deleteReservaKart(1L, 1L);

        // Then
        verify(reservaKartRepository).deleteById(reservaKartId);
    }

    @Test
    void deleteReservaKart_noExisteReservaKart_ShouldThrowException() {
        // Given
        when(reservaKartRepository.existsById(reservaKartId)).thenReturn(false);

        // When y Then
        assertThrows(RuntimeException.class, () -> {
            reservaKartService.deleteReservaKart(1L, 1L);
        });
    }

    @Test
    void deleteAllByReserva_deberiaEliminarTodasRelacionesDeReserva() {
        // Given
        doNothing().when(reservaKartRepository).deleteByReservaIdReserva(1L);

        // When
        reservaKartService.deleteAllByReserva(1L);

        // Then
        verify(reservaKartRepository).deleteByReservaIdReserva(1L);
    }
}