package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.ProductDto;
import com.pizzaria_system.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<ProductDto> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public ProductDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public ProductDto create(@RequestBody ProductDto Product) {
        return service.create(Product);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProductDto update(@PathVariable("id") Long id, @RequestBody ProductDto Product) {
        Product.setId(id);
        return service.update(Product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
