package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
import com.ms_security.ms_security.persistence.repository.IOrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderItemConsultations {

    private final IOrderItemRepository _orderItemRepository;

    @Cacheable(value = "OrderItemFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<OrderItemEntity> findById(Long id) {
        return _orderItemRepository.findById(id);
    }

    @Cacheable(value = "OrderItemFindAll")
    @Transactional(readOnly = true)
    public Page<OrderItemEntity> findAll(Pageable pageable) {
        return _orderItemRepository.findAll(pageable);
    }

    @CacheEvict(value = {"OrderItemFindById", "OrderItemFindAll"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderItemEntity addNew(OrderItemEntity entity) {
        return _orderItemRepository.save(entity);
    }

    @CacheEvict(value = {"OrderItemFindById", "OrderItemFindAll"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderItemEntity updateData(OrderItemEntity entity) {
        return _orderItemRepository.save(entity);
    }
}
