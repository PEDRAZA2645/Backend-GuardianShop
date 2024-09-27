package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.CartEntity;
import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
import com.ms_security.ms_security.service.ICartService;
import com.ms_security.ms_security.service.impl.consultations.CartConsultations;
import com.ms_security.ms_security.service.impl.consultations.InventoryConsultations;
import com.ms_security.ms_security.service.model.dto.CartDto;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.OrderItemDto;
import com.ms_security.ms_security.utilities.CartUtilities;
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
public class CartImpl implements ICartService {

    private final CartConsultations _cartConsultations;
    private final InventoryConsultations _inventoryConsultations;
    private final ErrorControlUtilities _errorControlUtilities;

    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH CART BY ID");
        EncoderUtilities.validateBase64(encode);
        CartDto findByIdDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));

        Optional<CartEntity> cartEntity = _cartConsultations.findById(findByIdDto.getId());
        if (cartEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);

        CartDto cartDto = parse(cartEntity.get());
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(cartDto, 1L);
    }

    @Override
    public ResponseEntity<String> findAll(String encode) {
        log.info("SEARCH FOR ALL CARTS BEGINS");
        EncoderUtilities.validateBase64(encode);
        // Suponiendo que el encode es un DTO que tiene información de paginación
        FindByPageDto request = EncoderUtilities.decodeRequest(encode, FindByPageDto.class);
        EncoderUtilities.validator(request);
        log.info(EncoderUtilities.formatJson(request));

        Long pageSize = request.getSize() > 0 ? request.getSize() : 10L;
        Long pageId = request.getPage() > 0 ? request.getPage() : 1L;
        Pageable pageable = PaginationUtilities.createPageable(pageId.intValue(), pageSize.intValue(), "dateTimeCreation", "asc");

        Page<CartEntity> pageResult = _cartConsultations.findAll(pageable);
        List<CartDto> cartDtoList = pageResult.stream().map(this::parse).toList();

        PageImpl<CartDto> response = new PageImpl<>(cartDtoList, pageable, pageResult.getTotalElements());
        log.info("SEARCH FOR ALL CARTS ENDED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }

    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("ADDING NEW CART BEGINS");

        CartDto cartDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        EncoderUtilities.validator(cartDto);
        log.info(EncoderUtilities.formatJson(cartDto));

        CartEntity cartEntity = parseEnt(cartDto, new CartEntity());
        cartEntity.setCreateUser(cartDto.getCreateUser());
        cartEntity.setDateTimeCreation(new Date().toString());

        CartEntity savedCart = _cartConsultations.addNew(cartEntity);
        CartDto savedCartDto = parse(savedCart);

        log.info("ADDING NEW CART ENDED");
        return _errorControlUtilities.handleSuccess(savedCartDto, 1L);
    }

    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATING CART BEGINS");

        CartDto cartDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        EncoderUtilities.validator(cartDto);
        log.info(EncoderUtilities.formatJson(cartDto));

        Optional<CartEntity> cartEntity = _cartConsultations.findById(cartDto.getId());
        if (cartEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);

        CartEntity existingCart = parseEnt(cartDto, cartEntity.get());
        existingCart.setUpdateUser(cartDto.getUpdateUser());
        existingCart.setDateTimeUpdate(new Date().toString());

        CartEntity updatedCart = _cartConsultations.updateData(existingCart);
        CartDto updatedCartDto = parse(updatedCart);

        log.info("UPDATING CART ENDED");
        return _errorControlUtilities.handleSuccess(updatedCartDto, 1L);
    }

    @Override
    public ResponseEntity<String> addToCart(String cartIdBase64, String inventoryIdBase64, String quantityBase64) {
        // Validar las entradas codificadas en base64
        EncoderUtilities.validateBase64(cartIdBase64);
        EncoderUtilities.validateBase64(inventoryIdBase64);
        EncoderUtilities.validateBase64(quantityBase64);

        log.info("ADDING ITEM TO CART BEGINS");

        // Decodificar los parámetros desde base64
        Long cartId = Long.valueOf(EncoderUtilities.decodeRequest(cartIdBase64, String.class));
        Long inventoryId = Long.valueOf(EncoderUtilities.decodeRequest(inventoryIdBase64, String.class));
        Long quantity = Long.valueOf(EncoderUtilities.decodeRequest(quantityBase64, String.class));

        log.info("Cart ID: {}, Inventory ID: {}, Quantity: {}", cartId, inventoryId, quantity);

        // Buscar el carrito por ID
        Optional<CartEntity> cartEntity = _cartConsultations.findById(cartId);
        if (cartEntity.isEmpty()) {
            return _errorControlUtilities.handleSuccess(null, 3L); // Código de error para "Carrito no encontrado"
        }

        // Buscar el inventario por ID
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findById(inventoryId);
        if (inventoryEntity.isEmpty() || inventoryEntity.get().getQuantity() < quantity) {
            return _errorControlUtilities.handleSuccess(null, 24L); // Código de error para "No hay suficiente inventario"
        }

        // Actualizar la cantidad del inventario
        InventoryEntity inventory = inventoryEntity.get();
        inventory.setQuantity(inventory.getQuantity() - quantity);
        _inventoryConsultations.updateData(inventory);

        // Agregar el ítem al carrito
        CartEntity cart = cartEntity.get();
        CartUtilities.addItemToCart(cart, inventory, quantity);

        // Actualizar información del carrito
        cart.setUpdateUser("system"); // Ajustar según el usuario actual
        cart.setDateTimeUpdate(new Date().toString());

        // Guardar los cambios en el carrito
        CartEntity updatedCart = _cartConsultations.updateData(cart);
        log.info("ITEM ADDED TO CART");

        return _errorControlUtilities.handleSuccess(parse(updatedCart), 1L);
    }

    @Override
    public ResponseEntity<String> removeFromCart(String cartIdBase64, String inventoryIdBase64) {
        // Validar las entradas codificadas en base64
        EncoderUtilities.validateBase64(cartIdBase64);
        EncoderUtilities.validateBase64(inventoryIdBase64);

        log.info("REMOVING ITEM FROM CART BEGINS");

        // Decodificar los parámetros desde base64
        Long cartId = Long.valueOf(EncoderUtilities.decodeRequest(cartIdBase64, String.class));
        Long inventoryId = Long.valueOf(EncoderUtilities.decodeRequest(inventoryIdBase64, String.class));

        log.info("Cart ID: {}, Inventory ID: {}", cartId, inventoryId);

        // Buscar el carrito por ID
        Optional<CartEntity> cartEntity = _cartConsultations.findById(cartId);
        if (cartEntity.isEmpty()) {
            return _errorControlUtilities.handleSuccess(null, 3L); // Código de error para "Carrito no encontrado"
        }

        // Buscar el inventario por ID
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findById(inventoryId);
        if (inventoryEntity.isEmpty()) {
            return _errorControlUtilities.handleSuccess(null, 24L); // Código de error para "Ítem no encontrado en inventario"
        }

        // Obtener el carrito y el ítem a eliminar
        CartEntity cart = cartEntity.get();
        boolean itemRemoved = CartUtilities.removeItemFromCart(cart, inventoryId);

        if (!itemRemoved) {
            return _errorControlUtilities.handleSuccess(null, 25L); // Código de error para "Ítem no encontrado en el carrito"
        }

        // Actualizar información del carrito
        cart.setUpdateUser("system"); // Ajustar según el usuario actual
        cart.setDateTimeUpdate(new Date().toString());

        // Guardar los cambios en el carrito
        CartEntity updatedCart = _cartConsultations.updateData(cart);
        log.info("ITEM REMOVED FROM CART");

        return _errorControlUtilities.handleSuccess(parse(updatedCart), 1L);
    }

    @Override
    public ResponseEntity<String> deleteCart(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("DELETING CART BEGINS");

        CartDto cartDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        EncoderUtilities.validator(cartDto);
        log.info(EncoderUtilities.formatJson(cartDto));

        Optional<CartEntity> cartEntity = _cartConsultations.findById(cartDto.getId());
        if (cartEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);

        _cartConsultations.delete(cartEntity.get());
        log.info("CART DELETED SUCCESSFULLY");

        return _errorControlUtilities.handleSuccess("Cart deleted successfully", 1L);
    }

    @Override
    public ResponseEntity<String> checkout(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("CHECKOUT PROCESS BEGINS");

        CartDto cartDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        EncoderUtilities.validator(cartDto);
        log.info(EncoderUtilities.formatJson(cartDto));

        Optional<CartEntity> cartEntity = _cartConsultations.findById(cartDto.getId());
        if (cartEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);

        CartEntity cart = cartEntity.get();
        cart.setStatus("COMPLETED");
        cart.setUpdateUser("system"); // Ajustar según el usuario actual
        cart.setDateTimeUpdate(new Date().toString());

        CartEntity checkedOutCart = _cartConsultations.updateData(cart);
        log.info("CHECKOUT PROCESS ENDED");
        return _errorControlUtilities.handleSuccess(parse(checkedOutCart), 1L);
    }

    private CartDto parse(CartEntity entity) {
        CartDto cartDto = new CartDto();
        cartDto.setId(entity.getId());
        cartDto.setStatus(entity.getStatus());
        cartDto.setCreateUser(entity.getCreateUser());
        cartDto.setUpdateUser(entity.getUpdateUser());

        // Convertir los items si existen
        if (entity.getItems() != null && !entity.getItems().isEmpty()) {
            List<OrderItemDto> itemDtos = entity.getItems().stream().map(itemEntity -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(itemEntity.getId());
                itemDto.setProductId(itemEntity.getProduct().getId());
                itemDto.setQuantity(itemEntity.getQuantity());
                return itemDto;
            }).toList();
            cartDto.setItems(itemDtos);
        }

        return cartDto;
    }

    private CartEntity parseEnt(CartDto dto, CartEntity entity) {
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setCreateUser(dto.getCreateUser());
        entity.setUpdateUser(dto.getUpdateUser());

        // Convertir los items si existen
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<OrderItemEntity> itemEntities = dto.getItems().stream().map(itemDto -> {
                OrderItemEntity itemEntity = new OrderItemEntity();
                itemEntity.setId(itemDto.getId());
                InventoryEntity inventory = new InventoryEntity();
                inventory.setId(itemDto.getProductId()); // O carga el objeto completo según sea necesario
                itemEntity.setProduct(inventory);
                itemEntity.setQuantity(itemDto.getQuantity());
                return itemEntity;
            }).toList();
            entity.setItems(itemEntities);
        }

        return entity;
    }
}
