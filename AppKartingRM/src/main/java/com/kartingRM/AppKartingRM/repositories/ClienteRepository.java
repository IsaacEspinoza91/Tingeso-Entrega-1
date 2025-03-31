package com.kartingRM.AppKartingRM.repositories;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    public ClienteEntity findByRut(String rut);
}
