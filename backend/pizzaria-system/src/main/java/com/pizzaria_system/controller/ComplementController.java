package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.ComplementDto;
import com.pizzaria_system.services.ComplementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complements")
public class ComplementController {
    @Autowired
    private ComplementService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<ComplementDto> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public ComplementDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public ComplementDto create(@RequestBody ComplementDto Complement) {
        return service.create(Complement);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ComplementDto update(@PathVariable("id") Long id, @RequestBody ComplementDto complement) {
        complement.setId(id);
        return service.update(complement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
