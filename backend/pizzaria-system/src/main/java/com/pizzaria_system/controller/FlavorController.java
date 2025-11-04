package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.FlavorDto;
import com.pizzaria_system.services.FlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flavor")
public class    FlavorController {
    @Autowired
    private FlavorService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<FlavorDto> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public FlavorDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public FlavorDto create(@RequestBody FlavorDto Flavor) {
        return service.create(Flavor);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public FlavorDto update(@PathVariable("id") Long id, @RequestBody FlavorDto Flavor) {
        Flavor.setId(id);
        return service.update(Flavor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
