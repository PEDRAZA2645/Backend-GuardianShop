package com.americandevops.middleware.service.implement;

import com.americandevops.middleware.persistence.entity.ErrorControlEntity;
import com.americandevops.middleware.service.implement.consultations.ErrorControlConsultations;
import com.americandevops.middleware.service.model.ErrorControlDto;
import com.americandevops.middleware.service.IErrorControlService;
import com.americandevops.middleware.service.model.FindByPageDto;
import com.americandevops.middleware.utilities.EncoderUtilities;
import com.americandevops.middleware.utilities.ErrorControlUtilities;
import com.americandevops.middleware.utilities.PaginationUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ErrorControlImpl implements IErrorControlService {

    private final ErrorControlConsultations _errorControlConsultations;
    private final ErrorControlUtilities _errorControlUtilities;

    @Override
    public ResponseEntity<String> findById(String encode) {
        EncoderUtilities.validateBase64(encode);
        ErrorControlDto dto = EncoderUtilities.decodeRequest(encode, ErrorControlDto.class);
        Optional<ErrorControlEntity> entity = _errorControlConsultations.findById(dto.getId());
        if (entity.isPresent()) {
            return _errorControlUtilities.handleSuccess(parse(entity.get()), 1L);
        }
        return _errorControlUtilities.handleSuccess(null, 3L);
    }

    @Override
    public ResponseEntity<String> findByErrorId(String encode) {
        EncoderUtilities.validateBase64(encode);
        ErrorControlDto dto = EncoderUtilities.decodeRequest(encode, ErrorControlDto.class);
        Optional<ErrorControlEntity> entity = _errorControlConsultations.findByErrorId(dto.getErrorId());
        if (entity.isPresent()) {
            return _errorControlUtilities.handleSuccess(parse(entity.get()), 1L);
        }
        return _errorControlUtilities.handleSuccess(null, 3L);
    }

    @Override
    public List<ErrorControlEntity> findAll() {
        return _errorControlConsultations.findAll();
    }

    @Override
    public ResponseEntity<String> findAll(String encode) {
        log.info("SEARCH FOR PAGES BEGINS");
        EncoderUtilities.validateBase64(encode);
        FindByPageDto request = EncoderUtilities.decodeRequest(encode, FindByPageDto.class);
        EncoderUtilities.validator(request);
        log.info(EncoderUtilities.formatJson(request));
        Long pageSize = request.getSize() > 0 ?  request.getSize() : 10L;
        Long pageId = request.getPage() > 0 ? request.getPage() : 1L;
        String sortBy = "dateTimeCreation";
        String direction = "asc";
        Pageable pageable = PaginationUtilities.createPageable(pageId.intValue(), pageSize.intValue(), sortBy, direction);
        Page<ErrorControlEntity> pageResult = _errorControlConsultations.findAll(pageable);
        List<ErrorControlDto> dtos = pageResult.stream().map(this::parse).toList();
        PageImpl<ErrorControlDto> response = new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
        log.info("SEARCH FOR PAGINATED ITEMS IS OVER");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }

    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        ErrorControlDto dto = EncoderUtilities.decodeRequest(encode, ErrorControlDto.class);
        log.info("STARTING INSERT");
        Optional<ErrorControlEntity> existingError = _errorControlConsultations.findByErrorIdOrErrorName(dto.getErrorId(), dto.getErrorName());
        if (existingError.isEmpty()) {
            ErrorControlEntity entity = parseEnt(dto, new ErrorControlEntity());
            entity.setDateTimeCreation(new Date());
            ErrorControlEntity savedEntity = _errorControlConsultations.addNew(entity);
            return _errorControlUtilities.handleSuccess(parse(savedEntity), 1L);
        }
        return _errorControlUtilities.handleSuccess(null, 10L);
    }

    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        ErrorControlDto dto = EncoderUtilities.decodeRequest(encode, ErrorControlDto.class);
        log.info("STARTING UPDATE");
        Optional<ErrorControlEntity> existingEntityOptional = _errorControlConsultations.findById(dto.getId());
        if (existingEntityOptional.isPresent()) {
            Optional<ErrorControlEntity> existingError = _errorControlConsultations.findByErrorName(dto.getErrorName());
            if (existingError.isEmpty()) {
                ErrorControlEntity entity = parseEnt(dto, existingEntityOptional.get());
                entity.setDateTimeUpdate(new Date());
                ErrorControlEntity updatedEntity = _errorControlConsultations.updateData(entity);
                return _errorControlUtilities.handleSuccess(parse(updatedEntity), 1L);
            }
            return _errorControlUtilities.handleSuccess(null, 11L);
        }
        return _errorControlUtilities.handleSuccess(null, 3L);
    }

    private ErrorControlDto parse(ErrorControlEntity entity) {
        ErrorControlDto dto = new ErrorControlDto();
        dto.setId(entity.getId());
        dto.setErrorId(entity.getErrorId());
        dto.setErrorName(entity.getErrorName());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }

    private ErrorControlEntity parseEnt(ErrorControlDto dto, ErrorControlEntity entity) {
        entity.setId(dto.getId());
        entity.setErrorId(dto.getErrorId());
        entity.setErrorName(dto.getErrorName());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setDateTimeCreation(entity.getDateTimeCreation());
        entity.setDateTimeUpdate(entity.getDateTimeUpdate());
        return entity;
    }
}
