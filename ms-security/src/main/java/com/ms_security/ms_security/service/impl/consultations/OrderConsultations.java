package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.OrderEntity;
import com.ms_security.ms_security.persistence.repository.IOrderRepository;
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
public class OrderConsultations {

    private final IOrderRepository _orderRepository;

    @Cacheable(value = "OrderFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<OrderEntity> findById(Long id) {
        return _orderRepository.findById(id);
    }

    @Cacheable(value = "OrderFindAll")
    @Transactional(readOnly = true)
    public Page<OrderEntity> findAll(Pageable pageable) {
        return _orderRepository.findAll(pageable);
    }

    @CacheEvict(value = {"OrderFindById", "OrderFindAll"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderEntity addNew(OrderEntity entity) {
        return _orderRepository.save(entity);
    }

    @CacheEvict(value = {"OrderFindById", "OrderFindAll"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderEntity updateData(OrderEntity entity) {
        return _orderRepository.save(entity);
    }
}
