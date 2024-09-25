package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.ServicesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IServicesRepository extends JpaRepository<ServicesEntity, Long> {
    Page<ServicesEntity> findAll(Pageable pageable);
    Optional<ServicesEntity> findByName(String name);
}
