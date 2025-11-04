package com.pizzaria_system.repository;

import com.pizzaria_system.model.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TableEntityRepository extends JpaRepository<TableEntity, Long> {

    @Query("SELECT t FROM TableEntity t " +
            "LEFT JOIN FETCH t.order o " +
            "LEFT JOIN FETCH o.items i " +
            "WHERE t.id = :id")
    Optional<TableEntity> findByIdWithOrderAndItems(@Param("id") Long id);
}
