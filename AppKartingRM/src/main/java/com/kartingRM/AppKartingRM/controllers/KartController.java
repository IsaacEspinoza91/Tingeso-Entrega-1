package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.KartEntity;
import com.kartingRM.AppKartingRM.services.KartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/karts")
public class KartController {

    @Autowired
    private KartService kartService;

    @GetMapping("/")
    public ResponseEntity<List<KartEntity>> getKarts(){
        List<KartEntity> karts = kartService.getKarts();
        return ResponseEntity.ok(karts);
    }

    @GetMapping("/{id_kart}")
    public ResponseEntity<KartEntity> getKartById(@PathVariable Long id_kart){
        KartEntity kart = kartService.getKartById(id_kart);
        return ResponseEntity.ok(kart);
    }

    @PostMapping("/")
    public ResponseEntity<KartEntity> createKart(@RequestBody KartEntity kart){
        KartEntity kartNuevo = kartService.createKart(kart);
        return ResponseEntity.ok(kartNuevo);
    }

    @PutMapping("/{id_kart}")
    public ResponseEntity<KartEntity> updateKart(@PathVariable Long id_kart, @RequestBody KartEntity kart){
        KartEntity kartActualizado = kartService.updateKart(id_kart, kart);
        return ResponseEntity.ok(kartActualizado);
    }

    @DeleteMapping("/{id_kart}")
    public ResponseEntity<Boolean> deleteKartById(@PathVariable Long id) throws Exception {
        var isDeleted = kartService.deleteKart(id);
        return ResponseEntity.noContent().build();
    }
}
