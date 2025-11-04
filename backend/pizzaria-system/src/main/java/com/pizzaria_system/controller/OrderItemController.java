package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.OrderItemDto;
import com.pizzaria_system.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/order_item")
@RestController
public class OrderItemController {
    @Autowired
    private OrderItemService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<OrderItemDto> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public OrderItemDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public OrderItemDto create(@RequestBody OrderItemDto OrderItem) {
        return service.create(OrderItem);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderItemDto update(@PathVariable("id") Long id, @RequestBody OrderItemDto OrderItem) {
        OrderItem.setId(id);
        return service.update(OrderItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
