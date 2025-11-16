package com.pizzaria_system.repository;

import com.pizzaria_system.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {
    <T> Optional<T> findActiveByTableId(Long tableId);

    @Query("SELECT DISTINCT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.payments " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.productVariation " +
            "LEFT JOIN FETCH o.client " +
            "LEFT JOIN FETCH o.user " +
            "LEFT JOIN FETCH o.table")
    List<OrderEntity> findAllWithPayments();

    // Método para buscar order específico com todas as relações
    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.payments " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.productVariation " +
            "LEFT JOIN FETCH o.client " +
            "LEFT JOIN FETCH o.user " +
            "LEFT JOIN FETCH o.table " +
            "WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithPayments(@Param("id") Long id);

}
