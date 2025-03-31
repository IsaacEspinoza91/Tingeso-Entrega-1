package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.KartEntity;
import com.kartingRM.AppKartingRM.repositories.KartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KartService {

    @Autowired
    private KartRepository kartRepository;

    public List<KartEntity> getKarts(){
        return kartRepository.findAll();
    }

    public KartEntity getKartById(Long id){
        return kartRepository.findById(id).get();
    }

    public KartEntity createKart(KartEntity kart){
        return kartRepository.save(kart);
    }

    public KartEntity updateKart(Long id, KartEntity kart){
        kart.setIdkart(id);
        return kartRepository.save(kart);
    }

    public boolean deleteKart (Long id) throws Exception{
        try {
            kartRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
