package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.KartEntity;
import com.kartingRM.AppKartingRM.repositories.KartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class KartServiceTest {

    @Mock
    private KartRepository kartRepository;

    @InjectMocks
    private KartService kartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getKarts_deberiaRetornarListaKarts() {
        // given
        List<KartEntity> karts = List.of(
                new KartEntity(1L, "ModeloA", "Disponible"),
                new KartEntity(2L, "ModeloB", "En uso")
        );
        when(kartRepository.findAll()).thenReturn(karts);

        // when
        List<KartEntity> result = kartService.getKarts();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getModelo()).isEqualTo("ModeloA");
    }

    @Test
    void getKartById_deberiaRetornarKart() {
        // given
        KartEntity kart = new KartEntity(1L, "ModeloA", "Disponible");
        when(kartRepository.findById(1L)).thenReturn(Optional.of(kart));

        // when
        KartEntity result = kartService.getKartById(1L);

        // then
        assertThat(result.getModelo()).isEqualTo("ModeloA");
    }

    @Test
    void getKartById_NoExisteKart_deberiaLanzarNoSuchElementException() {
        // given
        when(kartRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(NoSuchElementException.class, () -> kartService.getKartById(99L));
    }

    @Test
    void createKart_deberiaRetornarKartGuardado() {
        // given
        KartEntity kart = new KartEntity(null, "ModeloC", "Disponible");
        KartEntity savedKart = new KartEntity(3L, "ModeloC", "Disponible");

        when(kartRepository.save(kart)).thenReturn(savedKart);

        // when
        KartEntity result = kartService.createKart(kart);

        // then
        assertThat(result.getIdkart()).isEqualTo(3L);
    }

    @Test
    void deleteKart_deberiaRetornarTrue() throws Exception {
        // given
        doNothing().when(kartRepository).deleteById(1L);

        // when
        boolean result = kartService.deleteKart(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void deleteKart_deberiaLanzarExcepcion() {
        // given
        doThrow(new RuntimeException("Error")).when(kartRepository).deleteById(1L);

        // when / then
        assertThatThrownBy(() -> kartService.deleteKart(1L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Error");
    }

    @Test
    void updateKart_deberiaRetornarKartActualizado() {
        // given
        KartEntity existingKart = new KartEntity(1L, "ModeloAntiguo", "En uso");
        KartEntity newKartData = new KartEntity(null, "ModeloNuevo", "Disponible");

        when(kartRepository.findById(1L)).thenReturn(Optional.of(existingKart));
        when(kartRepository.save(existingKart)).thenReturn(existingKart);
        when(kartRepository.save(any(KartEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        KartEntity result = kartService.updateKart(1L, newKartData);

        // then
        assertThat(result.getModelo()).isEqualTo("ModeloNuevo");
        assertThat(result.getEstado()).isEqualTo("Disponible");

    }
}
