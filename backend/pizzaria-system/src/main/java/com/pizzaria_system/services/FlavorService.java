package com.pizzaria_system.services;

import com.pizzaria_system.controller.FlavorController;
import com.pizzaria_system.data.dto.FlavorDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.Flavor;
import com.pizzaria_system.repository.FlavorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class FlavorService {
    private Logger logger = LoggerFactory.getLogger(FlavorService.class.getName());

    @Autowired
    private FlavorRepository repository;

    public List<FlavorDto> findAll() {
        logger.info("Finding all Flavors");
        var Flavors = ObjectMapper.parseListObject(repository.findAll(), FlavorDto.class);
        Flavors.forEach(this::addHateoasLinks);
        return Flavors;
    }

    public FlavorDto findById(Long id) {
        logger.info("Finding one Flavor");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Flavor not found for this id"));
        var dto = ObjectMapper.parseObject(entity, FlavorDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public FlavorDto create(FlavorDto Flavor) {
        logger.info("Creating one Flavors");

        if(Flavor == null) throw new RequireObjectIsNullException("Category not found");

        var entity = ObjectMapper.parseObject(Flavor, Flavor.class);
        repository.save(entity);
        var  dto = ObjectMapper.parseObject(entity, FlavorDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public FlavorDto update(FlavorDto Flavor) {
        logger.info("Updating all Flavors");

        if(Flavor == null) throw new RequireObjectIsNullException("Category not found");

        var entity = repository.findById(Flavor.getId()).orElseThrow(() -> new ResourceNotFoundException("Flavor not found for this id!"));
        entity.setName(Flavor.getName());
        entity.setDescription(Flavor.getDescription());

        var FlavorUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(FlavorUpdated, FlavorDto.class);
        addHateoasLinks(dto);
        return dto;

    }

    public void delete(Long id) {
        logger.info("Deleting one Flavor");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("Flavor not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(FlavorDto dto) {
        dto.add(linkTo(methodOn(FlavorController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(FlavorController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(FlavorController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(FlavorController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(FlavorController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
