package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.PlanEntity;
import com.kartingRM.AppKartingRM.repositories.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public List<PlanEntity> getPlanes(){
        return planRepository.findAll();
    }

    public PlanEntity getPlanById(Long id){
        return planRepository.findById(id).get();
    }

    public PlanEntity createPlan(PlanEntity plan){
        return planRepository.save(plan);
    }

    public PlanEntity updatePlan(Long id, PlanEntity plan){
        plan.setIdPlan(id);
        return planRepository.save(plan);
    }

    public boolean deletePlan(Long id) throws Exception {
        try{
            planRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
}
