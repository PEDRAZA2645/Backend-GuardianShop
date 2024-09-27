package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.CartEntity;
import com.ms_security.ms_security.persistence.repository.ICartRepository;
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
public class CartConsultations {

    private final ICartRepository _cartRepository;

    @Cacheable(value = "CartFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<CartEntity> findById(Long id) {
        return _cartRepository.findById(id);
    }

    @Cacheable(value = "CartFindAll")
    @Transactional(readOnly = true)
    public Page<CartEntity> findAll(Pageable pageable) {
        return _cartRepository.findAll(pageable);
    }

    @Cacheable(value = "CartFindByUserId", key = "#userId")
    @Transactional(readOnly = true)
    public Optional<CartEntity> findByUserId(Long userId) {
        return _cartRepository.findByUserId(userId);
    }

    @CacheEvict(value = {"CartFindById", "CartFindAll"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public CartEntity addNew(CartEntity entity) {
        return _cartRepository.save(entity);
    }

    @CacheEvict(value = {"CartFindById", "CartFindAll"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public CartEntity updateData(CartEntity entity) {
        return _cartRepository.save(entity);
    }

    @CacheEvict(value = {"CartFindById", "CartFindAll"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void delete(CartEntity id) {
        _cartRepository.delete(id);
    }
}
