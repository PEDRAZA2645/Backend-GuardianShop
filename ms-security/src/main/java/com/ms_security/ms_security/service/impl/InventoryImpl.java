package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.service.IInventoryService;
import com.ms_security.ms_security.service.impl.consultations.InventoryConsultations;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.InventoryDto;
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
public class InventoryImpl implements IInventoryService {

    private final InventoryConsultations _inventoryConsultations;
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
        InventoryDto findByIdDto = EncoderUtilities.decodeRequest(encode, InventoryDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findById(findByIdDto.getId());
        if(inventoryEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        InventoryEntity inventory = inventoryEntity.get();
        InventoryDto inventoryDto = parse(inventory);
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(inventoryDto, 1L);
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
        Long pageSize = request.getSize() > 0 ? request.getSize() : 10L;
        Long pageId = request.getPage() > 0 ? request.getPage() : 1L;
        String sortBy = "dateTimeCreation";
        String direction = "asc";
        Pageable pageable = PaginationUtilities.createPageable(pageId.intValue(), pageSize.intValue(), sortBy, direction);
        Page<InventoryEntity> pageResult = _inventoryConsultations.findAll(pageable);
        List<InventoryDto> inventoryDto = pageResult.stream().map(this::parse).toList();
        PageImpl<InventoryDto> response = new PageImpl<>(inventoryDto, pageable, pageResult.getTotalElements());
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
        InventoryDto inventoryDto = EncoderUtilities.decodeRequest(encode, InventoryDto.class);
        EncoderUtilities.validator(inventoryDto, InventoryDto.Create.class);
        log.info(EncoderUtilities.formatJson(inventoryDto));
        log.info("START SEARCH BY CODE");
        Optional<InventoryEntity> code = _inventoryConsultations.findByCode(inventoryDto.getCode());
        if (code.isPresent()) return _errorControlUtilities.handleSuccess(null, 21L);
        log.info("END SEARCH BY CODE");
        log.info("START SEARCH BY CATEGORY AND PRODUCT");
        Optional<InventoryEntity> entity = _inventoryConsultations.findByCodeAndCategoryId(inventoryDto.getCategoryId(), inventoryDto.getProductId());
        if (entity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 23L);
        log.info("END SEARCH BY CATEGORY AND PRODUCT");
        InventoryEntity existingEntity = parseEnt(inventoryDto, new InventoryEntity());
        existingEntity.setCreateUser(inventoryDto.getCreateUser());
        existingEntity.setDateTimeCreation(new Date().toString());
        InventoryEntity inventoryEntity = _inventoryConsultations.addNew(existingEntity);
        InventoryDto inventoryDtos = parse(inventoryEntity);
        log.info("INSERT ENDED");
        return _errorControlUtilities.handleSuccess(inventoryDtos, 1L);
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
        log.info("UPDATE BEGINS");
        InventoryDto inventoryDto = EncoderUtilities.decodeRequest(encode, InventoryDto.class);
        EncoderUtilities.validator(inventoryDto, InventoryDto.Update.class);
        log.info(EncoderUtilities.formatJson(inventoryDto));
        log.info("START SEARCH BY ID");
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findById(inventoryDto.getId());
        log.info("END SEARCH BY ID");
        if (inventoryEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        log.info("START SEARCH BY CODE");
        InventoryEntity code = inventoryEntity.get();
        if (!code.getCode().equals(inventoryDto.getCode())) return _errorControlUtilities.handleSuccess(null, 22L);
        log.info("END SEARCH BY CODE");
        log.info("START SEARCH BY CATEGORY AND PRODUCT");
        Optional<InventoryEntity> entity = _inventoryConsultations.findByCodeAndCategoryId(inventoryDto.getCategoryId(), inventoryDto.getProductId());
        if (entity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 23L);
        log.info("END SEARCH BY CATEGORY AND PRODUCT");
        InventoryEntity existingEntity = parseEnt(inventoryDto, inventoryEntity.get());
        existingEntity.setUpdateUser(inventoryDto.getUpdateUser());
        existingEntity.setDateTimeUpdate(new Date().toString());
        InventoryEntity updatedInventory = _inventoryConsultations.updateData(existingEntity);
        InventoryDto inventoryDtos = parse(updatedInventory);
        log.info("UPDATE ENDED");
        return _errorControlUtilities.handleSuccess(inventoryDtos, 1L);
    }

    /**
     * Converts an InventoryEntity object to an InventoryDto object.
     *
     * @param entity The InventoryEntity object to be converted.
     * @return The corresponding InventoryDto object.
     */
    private InventoryDto parse(InventoryEntity entity) {
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setId(entity.getId());
        inventoryDto.setCode(entity.getCode());
        inventoryDto.setName(entity.getName());
        inventoryDto.setQuantity(entity.getQuantity());
        inventoryDto.setCost(entity.getCost());
        inventoryDto.setStatus(entity.getStatus());
        inventoryDto.setCategoryId(entity.getCategoryId());
        inventoryDto.setCreateUser(entity.getCreateUser());
        inventoryDto.setUpdateUser(entity.getUpdateUser());
        return inventoryDto;
    }

    /**
     * Converts an InventoryDto object to an InventoryEntity object.
     *
     * @param dto The InventoryDto object to be converted.
     * @param entity The InventoryEntity object to be updated.
     * @return The updated InventoryEntity object.
     */
    private InventoryEntity parseEnt(InventoryDto dto, InventoryEntity entity) {
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setQuantity(dto.getQuantity());
        entity.setCost(dto.getCost());
        entity.setStatus(dto.getStatus());
        entity.setCategoryId(dto.getCategoryId());
        entity.setCreateUser(entity.getCreateUser());
        entity.setUpdateUser(entity.getUpdateUser());
        entity.setDateTimeCreation(entity.getDateTimeCreation());
        entity.setDateTimeUpdate(entity.getDateTimeUpdate());
        return entity;
    }
}
