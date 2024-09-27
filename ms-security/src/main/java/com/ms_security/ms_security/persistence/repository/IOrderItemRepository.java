package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    Page<OrderItemEntity> findAll(Pageable pageable);
}
