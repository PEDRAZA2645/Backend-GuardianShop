package com.americandevops.middleware.persistence.repository;

import com.americandevops.middleware.persistence.entity.ErrorControlEntity;
import com.americandevops.middleware.service.model.ErrorControlDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IErrorControlRepository extends JpaRepository<ErrorControlEntity, Long> {

    Optional<ErrorControlEntity> findByErrorIdOrErrorName(Long idError, String idName);
    Optional<ErrorControlEntity> findByErrorName( String errorName);
    Optional<ErrorControlEntity> findByErrorId( Long errorId);
    Page<ErrorControlEntity> findAll(Pageable pageable);
}


