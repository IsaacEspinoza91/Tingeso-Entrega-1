package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.PlanEntity;
import com.kartingRM.AppKartingRM.repositories.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public List<PlanEntity> getPlanes(){
        return planRepository.findAll();
    }

    public PlanEntity getPlanById(Long id) {
        Optional<PlanEntity> planOptional = planRepository.findById(id);
        if (planOptional.isEmpty()) {
            throw new EntityNotFoundException("Plan con ID " + id + " no encontrado");
        }
        return planOptional.get();
    }

    public PlanEntity createPlan(PlanEntity plan){
        return planRepository.save(plan);
    }

    public PlanEntity updatePlan(Long id, PlanEntity plan){
        plan.setIdPlan(id);
        return planRepository.save(plan);
    }

    public boolean deletePlan(Long id) throws EntityNotFoundException {
        if (!planRepository.existsById(id)) {
            throw new EntityNotFoundException("Plan con ID " + id + " no encontrado");
        }
        planRepository.deleteById(id);
        return true;
    }
}
