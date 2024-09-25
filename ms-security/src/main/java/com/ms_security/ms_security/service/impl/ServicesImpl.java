package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.ServicesEntity;
import com.ms_security.ms_security.service.impl.consultations.ServicesConsultations;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.ServicesDto;
import com.ms_security.ms_security.service.IServicesService;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import com.ms_security.ms_security.utilities.PaginationUtilities;
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


@Log4j2
@Service
@RequiredArgsConstructor
public class ServicesImpl implements IServicesService {
    
    private final ServicesConsultations _servicesConsultations;
    private final ErrorControlUtilities _errorControlUtilities;

    /**
     * Method responsible for searching for a record by its ID.
     *
     * @param encode Base64 encoded request containing the ID of the record to be retrieved.
     * @return A ResponseEntity object containing the requested data or an error message.
     */
    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH BY ID");
        EncoderUtilities.validateBase64(encode);
        ServicesDto findByIdDto = EncoderUtilities.decodeRequest(encode, ServicesDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<ServicesEntity> servicesEntity = _servicesConsultations.findById(findByIdDto.getId());
        if(servicesEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        ServicesEntity services = servicesEntity.get();
        ServicesDto servicesDto = parse(services);
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(servicesDto, 1L);
    }

    /**
     * Retrieves all records with pagination.
     *
     * @param encode Base64 encoded request containing pagination information.
     * @return A ResponseEntity object containing a paginated list of records or an error message.
     */
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
        Page<ServicesEntity> pageResult = _servicesConsultations.findAll(pageable);
        List<ServicesDto> serviceDto = pageResult.stream().map(this::parse).toList();
        PageImpl<ServicesDto> response = new PageImpl<>(serviceDto, pageable, pageResult.getTotalElements());
        log.info("SEARCH FOR PAGINATED ITEMS IS OVER");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }

    /**
     * Responsible for creating a new record.
     *
     * @param encode Base64 encoded request containing the details of the record to be created.
     * @return A ResponseEntity object containing the created record or an error message.
     */
    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT BEGINS");
        ServicesDto servicesDto = EncoderUtilities.decodeRequest(encode, ServicesDto.class);
        EncoderUtilities.validator(servicesDto, ServicesDto.Create.class);
        log.info(EncoderUtilities.formatJson(servicesDto));
        log.info("START SEARCH BY NAME");
        Optional<ServicesEntity> name = _servicesConsultations.findByName(servicesDto.getName());
        if (name.isPresent()) return _errorControlUtilities.handleSuccess(null, 10L);
        log.info("END SEARCH BY NAME");
        ServicesEntity existingEntity = parseEnt(servicesDto, new ServicesEntity());
        existingEntity.setCreateUser(servicesDto.getCreateUser());
        existingEntity.setDateTimeCreation(new Date().toString());
        ServicesEntity servicesEntity = _servicesConsultations.addNew(existingEntity);
        ServicesDto servicesDtos = parse(servicesEntity);
        log.info("INSERT ENDED");
        return _errorControlUtilities.handleSuccess(servicesDtos, 1L);
    }

    /**
     * Responsible for updating an existing record.
     *
     * @param encode Base64 encoded request containing the details of the record to be updated.
     * @return A ResponseEntity object containing the updated record or an error message.
     */
    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT BEGINS");
        ServicesDto servicesDto = EncoderUtilities.decodeRequest(encode, ServicesDto.class);
        EncoderUtilities.validator(servicesDto, ServicesDto.Update.class);
        log.info(EncoderUtilities.formatJson(servicesDto));
        log.info("START SEARCH BY ID");
        Optional<ServicesEntity> servicesEntity = _servicesConsultations.findById(servicesDto.getId());
        log.info("START SEARCH BY ID");
        if (servicesEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        log.info("START SEARCH BY NAME");
        ServicesEntity servicesEntities = servicesEntity.get();
        if (!servicesEntities.getName().equals(servicesDto.getName())) return _errorControlUtilities.handleSuccess(null, 11L);
        log.info("END SEARCH BY NAME");
        ServicesEntity existingEntity = parseEnt(servicesDto, new ServicesEntity());
        existingEntity.setUpdateUser(servicesDto.getUpdateUser());
        existingEntity.setDateTimeUpdate(new Date().toString());
        ServicesEntity services = _servicesConsultations.addNew(existingEntity);
        ServicesDto servicesDtos = parse(services);
        log.info("INSERT ENDED");
        return _errorControlUtilities.handleSuccess(servicesDtos, 1L);
    }

    /**
     * Converts a ServicesEntity object to a ServicesDto object.
     *
     * @param entity The ServicesEntity object to be converted.
     * @return The corresponding ServicesDto object.
     */
    private ServicesDto parse(ServicesEntity entity){
        ServicesDto servicesDto = new ServicesDto();
        servicesDto.setId(entity.getId());
        servicesDto.setName(entity.getName());
        servicesDto.setDescription(entity.getDescription());
        servicesDto.setImageUrl(entity.getImageUrl());
        servicesDto.setStatus(entity.getStatus());
        servicesDto.setCreateUser(entity.getCreateUser());
        servicesDto.setUpdateUser(entity.getUpdateUser());
        return servicesDto;
    }

    /**
     * Converts a ServicesDto object to a ServicesEntity object.
     *
     * @param dto The ServicesDto object to be converted.
     * @param entity The ServicesEntity object to be updated.
     * @return The updated ServicesEntity object.
     */
    private ServicesEntity parseEnt(ServicesDto dto, ServicesEntity entity){
        ServicesEntity servicesEntity = new ServicesEntity();
        servicesEntity.setId(dto.getId());
        servicesEntity.setName(dto.getName());
        servicesEntity.setDescription(dto.getDescription());
        servicesEntity.setImageUrl(dto.getImageUrl());
        servicesEntity.setStatus(dto.getStatus());
        servicesEntity.setCreateUser(entity.getCreateUser());
        servicesEntity.setUpdateUser(entity.getUpdateUser());
        servicesEntity.setDateTimeCreation(entity.getDateTimeCreation());
        servicesEntity.setDateTimeUpdate(entity.getDateTimeCreation());
        return servicesEntity;
    }
}
