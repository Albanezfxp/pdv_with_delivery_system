package com.pizzaria_system.services;

import com.pizzaria_system.controller.UsuarioController;
import com.pizzaria_system.data.dto.UsuarioDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.Usuario;
import com.pizzaria_system.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UsuarioService {

    private Logger logger = LoggerFactory.getLogger(UsuarioService.class.getName());


    @Autowired
    UsuarioRepository repository;

    public List<UsuarioDto> findAll() {
        logger.info("Finding all users");
        var users = ObjectMapper.parseListObject(repository.findAll(), UsuarioDto.class);
        users.forEach(this::addHateoasLinks);
        return users;
    }

    public UsuarioDto findById(Long id) {
        logger.info("Finding one user");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id"));
        var dto = ObjectMapper.parseObject(entity, UsuarioDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public UsuarioDto create(UsuarioDto user) {
        logger.info("Creating one users");

        if(user == null) throw new RequireObjectIsNullException("Category not found");

        var entity = ObjectMapper.parseObject(user, Usuario.class);
        repository.save(entity);
        var  dto = ObjectMapper.parseObject(entity, UsuarioDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public UsuarioDto update(UsuarioDto user) {
        logger.info("Updating all users");

        if(user == null) throw new RequireObjectIsNullException("Category not found");

        var entity = repository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found for this id!"));
        entity.setActive(user.getActive());
        entity.setEmail(user.getEmail());
        entity.setName(user.getName());
        entity.setRole(user.getRole());
        entity.setPassword(user.getPassword());

        var userUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(userUpdated, UsuarioDto.class);
        addHateoasLinks(dto);
        return dto;

    }
    public void disableUser(Long id) {
        logger.info("Disabled user");

        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id!"));
        repository.disableUser(id);

    }

    public void delete(Long id) {
        logger.info("Deleting one user");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("User not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(UsuarioDto dto) {
        dto.add(linkTo(methodOn(UsuarioController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(UsuarioController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(UsuarioController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(UsuarioController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(UsuarioController.class).disableUser(dto.getId())).withRel("disable").withType("PATCH"));
        dto.add(linkTo(methodOn(UsuarioController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
