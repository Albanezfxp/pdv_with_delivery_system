package com.pizzaria_system.controller;


import com.pizzaria_system.data.dto.UsuarioDto;
import com.pizzaria_system.model.Usuario;
import com.pizzaria_system.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<UsuarioDto> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public UsuarioDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public UsuarioDto create(@RequestBody UsuarioDto user) {
        return service.create(user);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioDto update(@PathVariable("id") Long id, @RequestBody UsuarioDto userDto) {
     userDto.setId(id);
     return service.update(userDto);
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        service.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
