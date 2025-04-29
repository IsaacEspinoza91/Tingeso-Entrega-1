package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.PlanEntity;
import com.kartingRM.AppKartingRM.repositories.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPlanes_deberiaRetornarListaPlanes() {
        // given
        List<PlanEntity> planes = List.of(
                new PlanEntity(1L, "PlanA", 30, 10000, 12000, 14000),
                new PlanEntity(2L, "PlanB", 60, 18000, 20000, 25000)
        );
        when(planRepository.findAll()).thenReturn(planes);

        // when
        List<PlanEntity> result = planService.getPlanes();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(1).getDescripcion()).isEqualTo("PlanB");
    }

    @Test
    void getPlanById_Existente_deberiaRetornarPlan() {
        // given
        PlanEntity plan = new PlanEntity(1L, "PlanX", 20, 5000, 6000, 7000);
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        // when
        PlanEntity result = planService.getPlanById(1L);

        // then
        assertThat(result.getDescripcion()).isEqualTo("PlanX");
    }

    @Test
    void getPlanById_NoExistente_deberiaLanzarEntityNotFoundException() {
        // given
        when(planRepository.findById(42L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(EntityNotFoundException.class, () -> planService.getPlanById(42L));
    }

    @Test
    void createPlan_deberiaRetornarPlanGuardado() {
        // given
        PlanEntity plan = new PlanEntity(null, "PlanNuevo", 40, 11000, 13000, 15000);
        PlanEntity saved = new PlanEntity(5L, "PlanNuevo", 40, 11000, 13000, 15000);

        when(planRepository.save(plan)).thenReturn(saved);

        // when
        PlanEntity result = planService.createPlan(plan);

        // then
        assertThat(result.getIdPlan()).isEqualTo(5L);
    }

    @Test
    void updatePlan_deberiaRetornarPlanActualizado() {
        // given
        PlanEntity planActual = new PlanEntity(2L, "PlanAntiguo", 30, 9000, 11000, 13000);
        PlanEntity datosNuevos = new PlanEntity(null, "PlanActualizado", 45, 15000, 17000, 19000);

        when(planRepository.findById(2L)).thenReturn(Optional.of(planActual));
        when(planRepository.save(planActual)).thenReturn(planActual);
        when(planRepository.save(any(PlanEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PlanEntity result = planService.updatePlan(2L, datosNuevos);

        // then
        assertThat(result.getDescripcion()).isEqualTo("PlanActualizado");
        assertThat(result.getDuracionTotal()).isEqualTo(45);
    }

    @Test
    void deletePlan_Existente_deberiaRetornarTrue() throws Exception {
        // given
        when(planRepository.existsById(3L)).thenReturn(true);
        doNothing().when(planRepository).deleteById(3L);

        // when
        boolean result = planService.deletePlan(3L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void deletePlan_NoExistente_deberiaLanzarEntityNotFoundException() {
        // given
        when(planRepository.existsById(100L)).thenReturn(false);

        // when / then
        assertThrows(EntityNotFoundException.class, () -> planService.deletePlan(100L));
    }
}
