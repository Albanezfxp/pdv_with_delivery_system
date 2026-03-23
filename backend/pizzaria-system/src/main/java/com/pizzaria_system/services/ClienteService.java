package com.pizzaria_system.services;

import com.pizzaria_system.controller.ClienteController;
import com.pizzaria_system.data.dto.ClienteDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.Cliente;
import com.pizzaria_system.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ClienteService {
    private Logger logger = LoggerFactory.getLogger(ClienteService.class.getName());

    @Autowired
    private ClienteRepository repository;

    public Page<Cliente> findAll(Pageable pageable) {
        logger.info("Finding all clientes");
        var clients = repository.findAll(pageable);
        return  clients;
    }

    public ClienteDto findById(Long id) {
        logger.info("Finding one Cliente");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente not found for this id"));
        var dto = ObjectMapper.parseObject(entity, ClienteDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ClienteDto create(ClienteDto cliente) {
        logger.info("Creating one clientes");

        if(cliente == null) throw new RequireObjectIsNullException("Category not found");

        var entity = ObjectMapper.parseObject(cliente, Cliente.class);
        repository.save(entity);
        var  dto = ObjectMapper.parseObject(entity, ClienteDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ClienteDto update(ClienteDto cliente) {
        logger.info("Updating all clientes");

        if(cliente == null) throw new RequireObjectIsNullException("Category not found");

        var entity = repository.findById(cliente.getId()).orElseThrow(() -> new ResourceNotFoundException("Cliente not found for this id!"));
        entity.setName(cliente.getName());
        entity.setEndereco(cliente.getEndereco());
        entity.setBirthday(cliente.getBirthday());
        entity.setEmail(cliente.getEmail());
        entity.setPhone(cliente.getPhone());

        var ClienteUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(ClienteUpdated, ClienteDto.class);
        addHateoasLinks(dto);
        return dto;

    }

    public void delete(Long id) {
        logger.info("Deleting one Cliente");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("Cliente not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(ClienteDto dto) {
        dto.add(linkTo(methodOn(ClienteController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ClienteController.class).findAll(0, 10, "asc", "name")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ClienteController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ClienteController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ClienteController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
