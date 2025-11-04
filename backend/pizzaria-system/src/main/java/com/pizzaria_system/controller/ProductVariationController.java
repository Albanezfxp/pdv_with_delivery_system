package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.ProductVariationDto;
import com.pizzaria_system.services.ProductVariationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product_variation")
public class ProductVariationController {
    @Autowired
    private ProductVariationService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<ProductVariationDto> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public ProductVariationDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public ProductVariationDto create(@RequestBody ProductVariationDto ProductVariation) {
        return service.create(ProductVariation);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProductVariationDto update(@PathVariable("id") Long id, @RequestBody ProductVariationDto ProductVariation) {
        ProductVariation.setId(id);
        return service.update(ProductVariation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
