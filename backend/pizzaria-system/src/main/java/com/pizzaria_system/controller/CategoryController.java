package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.CategoryDto;
import com.pizzaria_system.model.Category;
import com.pizzaria_system.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Category> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public CategoryDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public CategoryDto create(@RequestBody CategoryDto user) {
        return service.create(user);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CategoryDto update(@PathVariable("id") Long id, @RequestBody CategoryDto clienteDto) {
        clienteDto.setId(id);
        return service.update(clienteDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
