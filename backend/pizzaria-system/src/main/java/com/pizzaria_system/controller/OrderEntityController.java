package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.OrderEntityDto;
import com.pizzaria_system.services.OrderEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order_entity")
public class OrderEntityController {
    @Autowired
    private OrderEntityService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<OrderEntityDto> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public OrderEntityDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public OrderEntityDto create(@RequestBody OrderEntityDto OrderEntity) {
        return service.create(OrderEntity);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderEntityDto update(@PathVariable("id") Long id, @RequestBody OrderEntityDto OrderEntity) {
        OrderEntity.setId(id);
        return service.update(OrderEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
