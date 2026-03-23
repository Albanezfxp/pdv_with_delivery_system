package com.pizzaria_system.services;

import com.pizzaria_system.controller.ProductController;
import com.pizzaria_system.data.dto.ProductDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.Product;
import com.pizzaria_system.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductService {

    private Logger logger = LoggerFactory.getLogger(ProductService.class.getName());

    @Autowired
    private ProductRepository repository;

    public List<ProductDto> findAll() {
        logger.info("Finding all Products");
        var Products = ObjectMapper.parseListObject(repository.findAll(), ProductDto.class).stream().filter(p -> p.getActive() == true).toList();
        Products.forEach(this::addHateoasLinks);
        return Products;
    }

    public ProductDto findById(Long id) {
        logger.info("Finding one Product");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found for this id"));
        var dto = ObjectMapper.parseObject(entity, ProductDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ProductDto create(ProductDto Product) {
        logger.info("Creating one Products");

        if(Product == null) throw new RequireObjectIsNullException("Category not found");

        var entity = ObjectMapper.parseObject(Product, Product.class);
        repository.save(entity);
        var  dto = ObjectMapper.parseObject(entity, ProductDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ProductDto update(ProductDto Product) {
        logger.info("Updating all Products");

        if(Product == null) throw new RequireObjectIsNullException("Category not found");

        var entity = repository.findById(Product.getId()).orElseThrow(() -> new ResourceNotFoundException("Product not found for this id!"));
        entity.setName(Product.getName());
        entity.setActive(Product.getActive());
        entity.setDescription(Product.getDescription());
        entity.setImageUrl(Product.getImageUrl());

        var ProductUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(ProductUpdated, ProductDto.class);
        addHateoasLinks(dto);
        return dto;

    }

    public void excludeProduct(Long id) {
        var product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setActive(false);

        repository.save(product);
    }

    public void delete(Long id) {
        logger.info("Deleting one Product");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("Product not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(ProductDto dto) {
        dto.add(linkTo(methodOn(ProductController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ProductController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ProductController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ProductController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ProductController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
    
}

