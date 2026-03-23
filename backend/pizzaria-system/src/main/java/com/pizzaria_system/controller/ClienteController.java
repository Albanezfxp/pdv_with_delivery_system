package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.ClienteDto;
import com.pizzaria_system.model.Cliente;
import com.pizzaria_system.services.ClienteService;
import org.hibernate.query.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<Cliente>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "directionParam", defaultValue = "name") String directionParam) {
        var sortDirection = "desc".equalsIgnoreCase(directionParam) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size,Sort.by(sortDirection,directionParam));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}")
    public ClienteDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public ClienteDto create(@RequestBody ClienteDto cliente) {
        return service.create(cliente);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDto update(@PathVariable("id") Long id, @RequestBody ClienteDto usreDto) {
        usreDto.setId(id);
        return service.update(usreDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
