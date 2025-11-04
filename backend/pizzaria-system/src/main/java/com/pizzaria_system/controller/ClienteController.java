package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.ClienteDto;
import com.pizzaria_system.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ClienteDto> findAll() {
        return service.findAll();
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
