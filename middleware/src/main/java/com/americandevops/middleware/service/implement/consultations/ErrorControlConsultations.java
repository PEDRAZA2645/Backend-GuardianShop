package com.americandevops.middleware.service.implement.consultations;

import com.americandevops.middleware.persistence.entity.ErrorControlEntity;
import com.americandevops.middleware.persistence.repository.IErrorControlRepository;
import com.americandevops.middleware.service.model.ErrorControlDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Log4j2
public class ErrorControlConsultations {

    private final IErrorControlRepository _iErrorControlRepository;
    @Transactional(readOnly = true)
    public Optional<ErrorControlEntity> findById(Long id) {
        return _iErrorControlRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ErrorControlEntity> findAll() {
        return _iErrorControlRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<ErrorControlEntity> findAll(Pageable pageable) {
        return _iErrorControlRepository.findAll(pageable);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ErrorControlEntity addNew(ErrorControlEntity solicitud) {
        return _iErrorControlRepository.save(solicitud);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ErrorControlEntity updateData(ErrorControlEntity solicitud) {
        return _iErrorControlRepository.save(solicitud);
    }

    @Transactional(readOnly = true)
    public Optional<ErrorControlEntity> findByErrorIdOrErrorName(Long errorId, String errorName) {
        return _iErrorControlRepository.findByErrorIdOrErrorName(errorId, errorName);
    }

    public Optional<ErrorControlEntity> findByErrorName( String errorName){
        return _iErrorControlRepository.findByErrorName(errorName);
    }

    public Optional<ErrorControlEntity> findByErrorId( Long errorId){
        return _iErrorControlRepository.findByErrorId(errorId);
    }
}
