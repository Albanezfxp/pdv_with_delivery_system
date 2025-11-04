package com.pizzaria_system.services;

import com.pizzaria_system.controller.ComplementController;
import com.pizzaria_system.data.dto.ComplementDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.Complement;
import com.pizzaria_system.repository.ComplementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ComplementService {
    private Logger logger = LoggerFactory.getLogger(ComplementService.class.getName());

    @Autowired
    private ComplementRepository repository;

    public List<ComplementDto> findAll() {
        logger.info("Finding all Complements");
        var Complements = ObjectMapper.parseListObject(repository.findAll(), ComplementDto.class);
        Complements.forEach(this::addHateoasLinks);
        return Complements;
    }

    public ComplementDto findById(Long id) {
        logger.info("Finding one Complement");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Complement not found for this id"));
        var dto = ObjectMapper.parseObject(entity, ComplementDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ComplementDto create(ComplementDto Complement) {
        logger.info("Creating one Complements");

        if(Complement == null) throw new RequireObjectIsNullException("Category not found");

        var entity = ObjectMapper.parseObject(Complement, Complement.class);
        repository.save(entity);
        var  dto = ObjectMapper.parseObject(entity, ComplementDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ComplementDto update(ComplementDto Complement) {
        logger.info("Updating all Complements");

        if(Complement == null) throw new RequireObjectIsNullException("Category not found");

        var entity = repository.findById(Complement.getId()).orElseThrow(() -> new ResourceNotFoundException("Complement not found for this id!"));
        entity.setName(Complement.getName());
        entity.setPrice(Complement.getPrice());
        entity.setType(Complement.getType());

        var ComplementUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(ComplementUpdated, ComplementDto.class);
        addHateoasLinks(dto);
        return dto;

    }

    public void delete(Long id) {
        logger.info("Deleting one Complement");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("Complement not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(ComplementDto dto) {
        dto.add(linkTo(methodOn(ComplementController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ComplementController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ComplementController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ComplementController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ComplementController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
