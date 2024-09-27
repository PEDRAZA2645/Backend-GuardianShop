package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.OrderEntity; // Asegúrate de tener esta entidad
import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
import com.ms_security.ms_security.service.IOrderItemService;
import com.ms_security.ms_security.service.IOrderService;
import com.ms_security.ms_security.service.impl.consultations.OrderConsultations; // Asegúrate de tener esta clase
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.InventoryDto;
import com.ms_security.ms_security.service.model.dto.OrderDto; // Asegúrate de tener este DTO
import com.ms_security.ms_security.service.model.dto.OrderItemDto;
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
public class OrderImpl implements IOrderService {

    private final OrderConsultations _orderConsultations;
    private final ErrorControlUtilities _errorControlUtilities;

    /**
     * Method responsible for searching for a record by its ID.
     *
     * @param encode ID of the order item to be retrieved.
     * @return A ResponseEntity object containing the requested data or an error message.
     */
    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH ORDER ITEM BY ID");
        EncoderUtilities.validateBase64(encode);
        OrderItemDto findByIdDto = EncoderUtilities.decodeRequest(encode, OrderItemDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<OrderEntity> orderEntity = _orderConsultations.findById(findByIdDto.getId());
        if (orderEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        OrderDto orderDto = parse(orderEntity.get());
        log.info("SEARCH ORDER ITEM BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(orderDto, 1L);
    }

    /**
     * Retrieves all records.
     *
     * @return A ResponseEntity object containing a list of all order items or an error message.
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
        Page<OrderEntity> pageResult = _orderConsultations.findAll(pageable);
        List<OrderDto> orderDtos = pageResult.stream().map(this::parse).toList();
        PageImpl<OrderDto> response = new PageImpl<>(orderDtos, pageable, pageResult.getTotalElements());
        log.info("SEARCH FOR ALL ORDER ITEMS IS OVER");
        return _errorControlUtilities.handleSuccess(orderDtos, 1L);
    }

    /**
     * Responsible for creating a new order item.
     *
     * @param encode Base64 encoded request containing the details of the order item to be created.
     * @return A ResponseEntity object containing the created order item or an error message.
     */
    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT ORDER ITEM BEGINS");
        OrderDto orderDto = EncoderUtilities.decodeRequest(encode, OrderDto.class);
        EncoderUtilities.validator(orderDto);
        log.info(EncoderUtilities.formatJson(orderDto));
        OrderEntity orderEntity = parseEnt(orderDto, new OrderEntity());
        orderEntity.setCreateUser(orderDto.getCreateUser());
        orderEntity.setDateTimeCreation(new Date().toString());
        OrderEntity createdOrder = _orderConsultations.addNew(orderEntity);
        OrderDto createdOrderDto = parse(createdOrder);
        log.info("INSERT ORDER ITEM ENDED");
        return _errorControlUtilities.handleSuccess(createdOrderDto, 1L);
    }

    /**
     * Responsible for updating an existing order item.
     *
     * @param encode Base64 encoded request containing the updated details.
     * @return A ResponseEntity object containing the updated order item or an error message.
     */
    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATE ORDER ITEM BEGINS");
        OrderDto orderDto = EncoderUtilities.decodeRequest(encode, OrderDto.class);
        EncoderUtilities.validator(orderDto);
        log.info(EncoderUtilities.formatJson(orderDto));
        Optional<OrderEntity> existingOrderEntity = _orderConsultations.findById(orderDto.getId());
        if (existingOrderEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        OrderEntity updatedOrderEntity = parseEnt(orderDto, existingOrderEntity.get());
        updatedOrderEntity.setUpdateUser(orderDto.getUpdateUser());
        updatedOrderEntity.setDateTimeUpdate(new Date().toString());
        updatedOrderEntity.setDateTimeOrder(new Date().toString());
        OrderEntity updatedOrder = _orderConsultations.updateData(updatedOrderEntity);
        OrderDto updatedOrderDto = parse(updatedOrder);
        log.info("UPDATE ORDER ITEM ENDED");
        return _errorControlUtilities.handleSuccess(updatedOrderDto, 1L);
    }

    /**
     * Converts an OrderEntity object to an OrderDto object.
     *
     * @param entity The OrderEntity object to be converted.
     * @return The corresponding OrderDto object.
     */
    private OrderDto parse(OrderEntity entity) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(entity.getId());
        orderDto.setUserId(entity.getUserId());
        orderDto.setTotalCost(entity.getTotalCost());
        orderDto.setStatus(entity.getStatus());
        orderDto.setCreateUser(entity.getCreateUser());
        orderDto.setUpdateUser(entity.getUpdateUser());

        // Convertir los items si existen
        if (entity.getItems() != null && !entity.getItems().isEmpty()) {
            List<OrderItemDto> itemDtos = entity.getItems().stream().map(itemEntity -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(itemEntity.getId());
                itemDto.setProductId(itemEntity.getProductId());
                itemDto.setQuantity(itemEntity.getQuantity());
                itemDto.setPrice(itemEntity.getPrice());
                return itemDto;
            }).toList();
            orderDto.setItems(itemDtos);
        }

        return orderDto;
    }


    /**
     * Converts an OrderDto object to an OrderEntity object.
     *
     * @param dto The OrderDto object to be converted.
     * @param entity The OrderEntity object to be updated.
     * @return The updated OrderEntity object.
     */
    private OrderEntity parseEnt(OrderDto dto, OrderEntity entity) {
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setTotalCost(dto.getTotalCost());
        entity.setStatus(dto.getStatus());
        entity.setCreateUser(dto.getCreateUser());
        entity.setUpdateUser(dto.getUpdateUser());

        // Convertir los items si existen
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<OrderItemEntity> itemEntities = dto.getItems().stream().map(itemDto -> {
                OrderItemEntity itemEntity = new OrderItemEntity();
                itemEntity.setId(itemDto.getId());
                itemEntity.setProductId(itemDto.getProductId());
                itemEntity.setQuantity(itemDto.getQuantity());
                itemEntity.setPrice(itemDto.getPrice());
                itemEntity.setOrder(entity); // Establecer la relación con el pedido actual
                return itemEntity;
            }).toList();
            entity.setItems(itemEntities);
        }

        return entity;
    }

}
