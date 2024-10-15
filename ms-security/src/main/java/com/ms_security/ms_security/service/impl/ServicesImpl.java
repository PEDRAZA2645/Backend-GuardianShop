package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.persistence.entity.ServicesEntity;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.IServicesService;
import com.ms_security.ms_security.service.impl.consultations.InventoryConsultations;
import com.ms_security.ms_security.service.impl.consultations.ServicesConsultations;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.ServicesDto;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Log4j2
@Service
@RequiredArgsConstructor
public class ServicesImpl implements IServicesService {
    
    private final ServicesConsultations _servicesConsultations;
    private final InventoryConsultations _inventoryConsultations;
    private final IParametersService _iParametersService;
    private final InventoryImpl _inventory;
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
        EncoderUtilities.validateBase64(encode);
        log.info("INITIATING PAGINATED SEARCH");
        FindByPageDto request = EncoderUtilities.decodeRequest(encode, FindByPageDto.class);
        EncoderUtilities.validator(request);
        log.info(EncoderUtilities.formatJson(request));
        log.info("INITIATING PARAMETER QUERY");
        Optional<ParametersEntity> pageSizeParam = _iParametersService.findByCodeParameter(1L);
        log.info("PARAMETER QUERY COMPLETED");
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                Integer.parseInt(pageSizeParam.get().getParameter()));
        Page<ServicesEntity> pageResult = _servicesConsultations.findAll(pageable);
        List<ServicesDto> serviceDto = pageResult.stream().map(this::parse).toList();
        PageImpl<ServicesDto> response = new PageImpl<>(serviceDto, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
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
        log.info("START SEARCH BY CODE");
        Optional<ServicesEntity> name = _servicesConsultations.findByCode(servicesDto.getCode());
        if (name.isPresent()) return _errorControlUtilities.handleSuccess(null, 21L);
        log.info("END SEARCH BY CODE");
        log.info("START SEARCH BY NAME");
        Optional<ServicesEntity> servicesEntities = _servicesConsultations.findByName(servicesDto.getName());
        if (servicesEntities.isPresent()) return _errorControlUtilities.handleSuccess(null, 10L);
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
    @Transactional
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATE SERVICE BEGINS");
        ServicesDto servicesDto = EncoderUtilities.decodeRequest(encode, ServicesDto.class);
        EncoderUtilities.validator(servicesDto, ServicesDto.Update.class);
        log.info(EncoderUtilities.formatJson(servicesDto));
        log.info("START SEARCH BY ID");
        Optional<ServicesEntity> servicesEntityOpt = _servicesConsultations.findByIdWithInventories(servicesDto.getId());
        if (servicesEntityOpt.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        ServicesEntity existingService = servicesEntityOpt.get();
        String newServiceName = servicesDto.getName();
        existingService.setName(newServiceName);
        List<InventoryEntity> relatedInventories = _inventoryConsultations.findAllByServiceId(existingService.getId());
        for (InventoryEntity inventory : relatedInventories) {
            inventory.setName(newServiceName + " " + inventory.getReference());
            inventory.setUpdateUser(servicesDto.getUpdateUser());
            inventory.setDateTimeUpdate(new Date().toString());
        }
        existingService.setSalePrice(relatedInventories.stream().findFirst().map(InventoryEntity::getSalePrice).orElse(BigDecimal.ZERO));
        existingService.setUpdateUser(servicesDto.getUpdateUser());
        _inventoryConsultations.updateBatch(relatedInventories);
        _servicesConsultations.updateData(existingService);
        ServicesDto servicesDtos = parse(existingService);
        log.info("UPDATE SERVICE ENDED");
        return _errorControlUtilities.handleSuccess(servicesDtos, 1L);
    }


    public void updateServiceSalePrice(Long productId, BigDecimal newPrice) {
        Optional<ServicesEntity> serviceOpt = _servicesConsultations.findById(productId);
        if (serviceOpt.isPresent()) {
            ServicesEntity service = serviceOpt.get();
            service.setSalePrice(newPrice); // Actualiza el precio
            _servicesConsultations.updateData(service); // Guarda los cambios
        }else _errorControlUtilities.handleSuccess(null,3L);
    }



    /**
     * Converts a ServicesEntity object to a ServicesDto object.
     *
     * @param entity The ServicesEntity object to be converted.
     * @return The corresponding ServicesDto object.
     */
    private ServicesDto parse(ServicesEntity entity) {
        ServicesDto servicesDto = new ServicesDto();
        servicesDto.setId(entity.getId());
        servicesDto.setCode(entity.getCode());
        servicesDto.setName(entity.getName());
        servicesDto.setDescription(entity.getDescription());
        servicesDto.setImageUrl(entity.getImageUrl());
        servicesDto.setSalePrice(entity.getSalePrice());
        servicesDto.setStatus(entity.getStatus());
        servicesDto.setCategoryId(entity.getCategoryId());
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
    private ServicesEntity parseEnt(ServicesDto dto, ServicesEntity entity) {
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setImageUrl(dto.getImageUrl());
        entity.setSalePrice(dto.getSalePrice());
        entity.setStatus(dto.getStatus());
        entity.setCategoryId(dto.getCategoryId());
        entity.setCreateUser(entity.getCreateUser());
        entity.setUpdateUser(dto.getUpdateUser());
        return entity;
    }

}
