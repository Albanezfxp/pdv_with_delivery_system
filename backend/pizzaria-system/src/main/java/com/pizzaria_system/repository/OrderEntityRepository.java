package com.pizzaria_system.repository;

import com.pizzaria_system.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {
    <T> Optional<T> findActiveByTableId(Long tableId);
}
