package com.pizzaria_system.repository;

import com.pizzaria_system.data.dto.OrderItemDto;
import com.pizzaria_system.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
