package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.PlanEntity;
import com.kartingRM.AppKartingRM.repositories.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PlanService.class) // Importamos el servicio para que Spring lo pueda inyectar
class PlanServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanService planService;

    @Test
    @DisplayName("Obtener todos los planes - Cuando hay planes registrados")
    void getPlanes_WhenPlansExist_ReturnsPlanList() {
        // Given
        PlanEntity plan1 = new PlanEntity(null, "Plan Básico", 30, 10000, 12000, 15000);
        PlanEntity plan2 = new PlanEntity(null, "Plan Premium", 60, 18000, 20000, 25000);
        entityManager.persist(plan1);
        entityManager.persist(plan2);
        entityManager.flush();

        // When
        List<PlanEntity> planes = planService.getPlanes();

        // Then
        assertEquals(2, planes.size());
    }

    @Test
    @DisplayName("Obtener plan por ID - Cuando el plan existe")
    void getPlanById_WhenPlanExists_ReturnsPlan() {
        // Given
        PlanEntity plan = new PlanEntity(null, "Plan Básico", 30, 10000, 12000, 15000);
        entityManager.persist(plan);
        entityManager.flush();
        Long id = plan.getIdPlan();

        // When
        PlanEntity foundPlan = planService.getPlanById(id);

        // Then
        assertNotNull(foundPlan);
        assertEquals("Plan Básico", foundPlan.getDescripcion());
        assertEquals(id, foundPlan.getIdPlan());
    }

    @Test
    @DisplayName("Obtener plan por ID - Cuando el plan no existe")
    void getPlanById_WhenPlanNotExists_ThrowsException() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        assertThrows(java.util.NoSuchElementException.class, () -> {
            planService.getPlanById(nonExistentId);
        });
    }

    @Test
    @DisplayName("Crear nuevo plan - Con datos válidos")
    void createPlan_WithValidData_ReturnsSavedPlan() {
        // Given
        PlanEntity newPlan = new PlanEntity(null, "Nuevo Plan", 45, 15000, 17000, 20000);

        // When
        PlanEntity savedPlan = planService.createPlan(newPlan);

        // Then
        assertNotNull(savedPlan.getIdPlan());
        assertEquals("Nuevo Plan", savedPlan.getDescripcion());
        assertEquals(45, savedPlan.getDuracionTotal());
    }

    @Test
    @DisplayName("Actualizar plan - Cuando el plan existe")
    void updatePlan_WhenPlanExists_ReturnsUpdatedPlan() {
        // Given
        PlanEntity originalPlan = new PlanEntity(null, "Plan Original", 30, 10000, 12000, 15000);
        entityManager.persist(originalPlan);
        entityManager.flush();
        Long id = originalPlan.getIdPlan();

        PlanEntity updatedData = new PlanEntity(null, "Plan Actualizado", 45, 15000, 17000, 20000);

        // When
        PlanEntity updatedPlan = planService.updatePlan(id, updatedData);

        // Then
        assertEquals(id, updatedPlan.getIdPlan());
        assertEquals("Plan Actualizado", updatedPlan.getDescripcion());
        assertEquals(45, updatedPlan.getDuracionTotal());
        assertEquals(15000, updatedPlan.getPrecioRegular());
    }

    /*
    @Test
    @DisplayName("Eliminar plan - Cuando el plan no existe")
    void deletePlan_WhenPlanNotExists_ThrowsException() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            planService.deletePlan(nonExistentId);
        });
    }*/

    @Test
    @DisplayName("Eliminar plan - Cuando el plan existe")
    void deletePlan_WhenPlanExists_ReturnsTrue() throws Exception {
        // Given
        PlanEntity plan = new PlanEntity(null, "Plan a Eliminar", 30, 10000, 12000, 15000);
        entityManager.persist(plan);
        entityManager.flush();
        Long id = plan.getIdPlan();

        // When
        boolean result = planService.deletePlan(id);

        // Then
        assertTrue(result);
        assertFalse(planRepository.existsById(id));
    }
}