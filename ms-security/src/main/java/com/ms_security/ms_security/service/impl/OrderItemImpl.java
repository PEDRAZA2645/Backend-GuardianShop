//package com.ms_security.ms_security.service.impl;
//
//import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
//import com.ms_security.ms_security.persistence.entity.ParametersEntity;
//import com.ms_security.ms_security.service.IOrderItemService;
//import com.ms_security.ms_security.service.IParametersService;
//import com.ms_security.ms_security.service.impl.consultations.OrderItemConsultations; // Asegúrate de tener esta clase
//import com.ms_security.ms_security.service.model.dto.FindByPageDto;
//import com.ms_security.ms_security.service.model.dto.OrderItemDto; // Asegúrate de tener este DTO
//import com.ms_security.ms_security.utilities.EncoderUtilities;
//import com.ms_security.ms_security.utilities.ErrorControlUtilities;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//@Log4j2
//@Service
//@RequiredArgsConstructor
//public class OrderItemImpl implements IOrderItemService {
//
//    private final OrderItemConsultations _orderItemConsultations;
//    private final IParametersService _iParametersService;
//    private final ErrorControlUtilities _errorControlUtilities;
//
//    /**
//     * Method responsible for searching for an order item by its ID.
//     *
//     * @param encode ID of the order item to be retrieved.
//     * @return A ResponseEntity object containing the requested data or an error message.
//     */
//    @Override
//    public ResponseEntity<String> findById(String encode) {
//        log.info("PROCEED TO SEARCH ORDER ITEM BY ID");
//        EncoderUtilities.validateBase64(encode);
//        OrderItemDto findByIdDto = EncoderUtilities.decodeRequest(encode, OrderItemDto.class);
//        EncoderUtilities.validator(findByIdDto);
//        log.info(EncoderUtilities.formatJson(findByIdDto));
//        Optional<OrderItemEntity> orderItemEntity = _orderItemConsultations.findById(findByIdDto.getId());
//        if (orderItemEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
//        OrderItemDto orderItemDto = parse(orderItemEntity.get());
//        log.info("SEARCH ORDER ITEM BY ID IS ENDED");
//        return _errorControlUtilities.handleSuccess(orderItemDto, 1L);
//    }
//
//    /**
//     * Retrieves all order items.
//     *
//     * @return A ResponseEntity object containing a list of all order items or an error message.
//     */
//    @Override
//    public ResponseEntity<String> findAll(String encode) {
//        EncoderUtilities.validateBase64(encode);
//        log.info("INITIATING PAGINATED SEARCH");
//        FindByPageDto request = EncoderUtilities.decodeRequest(encode, FindByPageDto.class);
//        EncoderUtilities.validator(request);
//        log.info(EncoderUtilities.formatJson(request));
//        log.info("INITIATING PARAMETER QUERY");
//        Optional<ParametersEntity> pageSizeParam = _iParametersService.findByCodeParameter(1L);
//        log.info("PARAMETER QUERY COMPLETED");
//        Pageable pageable = PageRequest.of(
//                request.getPage() - 1,
//                Integer.parseInt(pageSizeParam.get().getParameter()));
//        Page<OrderItemEntity> pageResult = _orderItemConsultations.findAll(pageable);
//        List<OrderItemDto> itemDto = pageResult.stream()
//                .map(this::parse)
//                .toList();
//        PageImpl<OrderItemDto> response = new PageImpl<>(itemDto, pageable, pageResult.getTotalElements());
//        log.info("PAGINATED SEARCH COMPLETED");
//        return _errorControlUtilities.handleSuccess(response, 1L);
//    }
//
//    /**
//     * Responsible for creating a new order item.
//     *
//     * @param encode Base64 encoded request containing the details of the order item to be created.
//     * @return A ResponseEntity object containing the created order item or an error message.
//     */
//    @Override
//    public ResponseEntity<String> addNew(String encode) {
//        EncoderUtilities.validateBase64(encode);
//        log.info("INSERT ORDER ITEM BEGINS");
//        OrderItemDto orderItemDto = EncoderUtilities.decodeRequest(encode, OrderItemDto.class);
//        EncoderUtilities.validator(orderItemDto);
//        log.info(EncoderUtilities.formatJson(orderItemDto));
//        OrderItemEntity orderItemEntity = parseEnt(orderItemDto, new OrderItemEntity());
//        orderItemEntity.setPrice(orderItemDto.getPrice());
//        orderItemEntity.setCreateUser(orderItemDto.getCreateUser());
//        orderItemEntity.setDateTimeCreation(new Date().toString());
//        OrderItemEntity createdOrderItem = _orderItemConsultations.addNew(orderItemEntity);
//        OrderItemDto createdOrderItemDto = parse(createdOrderItem);
//        log.info("INSERT ORDER ITEM ENDED");
//        return _errorControlUtilities.handleSuccess(createdOrderItemDto, 1L);
//    }
//
//    /**
//     * Responsible for updating an existing order item.
//     *
//     * @param encode Base64 encoded request containing the updated details.
//     * @return A ResponseEntity object containing the updated order item or an error message.
//     */
//    @Override
//    public ResponseEntity<String> updateData(String encode) {
//        EncoderUtilities.validateBase64(encode);
//        log.info("UPDATE ORDER ITEM BEGINS");
//        OrderItemDto orderItemDto = EncoderUtilities.decodeRequest(encode, OrderItemDto.class);
//        EncoderUtilities.validator(orderItemDto);
//        log.info(EncoderUtilities.formatJson(orderItemDto));
//        Optional<OrderItemEntity> existingOrderItemEntity = _orderItemConsultations.findById(orderItemDto.getId());
//        if (existingOrderItemEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
//        OrderItemEntity updatedOrderItemEntity = parseEnt(orderItemDto, existingOrderItemEntity.get());
//        updatedOrderItemEntity.setUpdateUser(orderItemDto.getUpdateUser());
//        updatedOrderItemEntity.setDateTimeUpdate(new Date().toString());
//        OrderItemEntity updatedOrderItem = _orderItemConsultations.updateData(updatedOrderItemEntity);
//        OrderItemDto updatedOrderItemDto = parse(updatedOrderItem);
//        log.info("UPDATE ORDER ITEM ENDED");
//        return _errorControlUtilities.handleSuccess(updatedOrderItemDto, 1L);
//    }
//
//    @Override
//    public ResponseEntity<String> deleteById(String encode) {
//        log.info("DELETE ORDER ITEM BEGINS");
//        EncoderUtilities.validateBase64(encode);
//        OrderItemDto orderItemDto = EncoderUtilities.decodeRequest(encode, OrderItemDto.class);
//        EncoderUtilities.validator(orderItemDto);
//        log.info(EncoderUtilities.formatJson(orderItemDto));
//        Optional<OrderItemEntity> existingOrderItemEntity = _orderItemConsultations.findById(orderItemDto.getId());
//        if (existingOrderItemEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
//        _orderItemConsultations.deleteById(orderItemDto.getId());
//        log.info("ORDER ITEM DELETED SUCCESSFULLY");
//        return _errorControlUtilities.handleSuccess(null, 1L);
//    }
//
//
//    /**
//     * Converts an OrderItemEntity object to an OrderItemDto object.
//     *
//     * @param entity The OrderItemEntity object to be converted.
//     * @return The corresponding OrderItemDto object.
//     */
//    private OrderItemDto parse(OrderItemEntity entity) {
//        OrderItemDto orderItemDto = new OrderItemDto();
//        orderItemDto.setId(entity.getId());
//        orderItemDto.setCartId(entity.getCartId());
//        orderItemDto.setProductId(entity.getProductId());
//        orderItemDto.setQuantity(entity.getQuantity());
//        orderItemDto.setCreateUser(entity.getCreateUser());
//        orderItemDto.setUpdateUser(entity.getUpdateUser());
//        return orderItemDto;
//    }
//
//    /**
//     * Converts an OrderItemDto object to an OrderItemEntity object.
//     *
//     * @param dto The OrderItemDto object to be converted.
//     * @param entity The OrderItemEntity object to be updated.
//     * @return The updated OrderItemEntity object.
//     */
//    private OrderItemEntity parseEnt(OrderItemDto dto, OrderItemEntity entity) {
//        entity.setId(dto.getId());
//        entity.setCartId(dto.getCartId());
//        entity.setProductId(dto.getProductId());
//        entity.setQuantity(dto.getQuantity());
//        entity.setCreateUser(dto.getCreateUser());
//        entity.setUpdateUser(dto.getUpdateUser());
//        return entity;
//    }
//}
