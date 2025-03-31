package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.PlanEntity;
import com.kartingRM.AppKartingRM.services.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planes")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping("/")
    public ResponseEntity<List<PlanEntity>> getPlanes(){
        List<PlanEntity> planEntities = planService.getPlanes();
        return ResponseEntity.ok(planEntities);
    }

    @GetMapping("/{id_plan}")
    public ResponseEntity<PlanEntity> getPlanById(@PathVariable Long id_plan){
        PlanEntity plan = planService.getPlanById(id_plan);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/")
    public ResponseEntity<PlanEntity> createPlan(@RequestBody PlanEntity plan){
        PlanEntity planNuevo = planService.createPlan(plan);
        return ResponseEntity.ok(planNuevo);
    }

    @PutMapping("/{id_plan}")
    public ResponseEntity<PlanEntity> updatePlan(@PathVariable Long id_plan, @RequestBody PlanEntity plan){
        PlanEntity planActualizado = planService.updatePlan(id_plan, plan);
        return ResponseEntity.ok(planActualizado);
    }

    @DeleteMapping("/{id_plan}")
    public ResponseEntity<Boolean> deletePlan(@PathVariable Long id_plan) throws Exception{
        planService.deletePlan(id_plan);
        return ResponseEntity.noContent().build();
    }
}
