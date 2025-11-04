package com.pizzaria_system.services;

import com.pizzaria_system.controller.CategoryController;
import com.pizzaria_system.data.dto.CategoryDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.Category;
import com.pizzaria_system.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryService.class.getName());

    @Autowired
    private CategoryRepository repository;

    public List<CategoryDto> findAll() {
        logger.info("Finding all categories");
        var categories = ObjectMapper.parseListObject(repository.findAll(), CategoryDto.class);
        categories.forEach(this::addHateoasLinks);
        return categories;
    }

    public CategoryDto findById(Long id) {
        logger.info("Finding one category");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("category not found for this id"));
        var dto = ObjectMapper.parseObject(entity, CategoryDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public CategoryDto create(CategoryDto category) {
        logger.info("Creating one categories");

        if(category == null) throw new RequireObjectIsNullException("Category not found");

        var entity = ObjectMapper.parseObject(category, Category.class);
        repository.save(entity);
        var  dto = ObjectMapper.parseObject(entity, CategoryDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public CategoryDto update(CategoryDto category) {
        logger.info("Updating all categories");

        if(category == null) throw new RequireObjectIsNullException("Category not found");

        var entity = repository.findById(category.getId()).orElseThrow(() -> new ResourceNotFoundException("category not found for this id!"));
        entity.setName(category.getName());
        entity.setProducts(category.getProducts());
        entity.setImageUrl(category.getImageUrl());
        var categoryUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(categoryUpdated, CategoryDto.class);
        addHateoasLinks(dto);
        return dto;

    }

    public void delete(Long id) {
        logger.info("Deleting one category");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("category not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(CategoryDto dto) {
        dto.add(linkTo(methodOn(CategoryController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CategoryController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(CategoryController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(CategoryController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(CategoryController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
