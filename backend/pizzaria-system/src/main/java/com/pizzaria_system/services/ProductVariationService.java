package com.pizzaria_system.services;

import com.pizzaria_system.controller.ProductVariationController;
import com.pizzaria_system.data.dto.ProductCreateVariationDto;
import com.pizzaria_system.data.dto.ProductVariationDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.ProductVariation;
import com.pizzaria_system.repository.ProductVariationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductVariationService {

    private Logger logger = LoggerFactory.getLogger(ProductVariationService.class.getName());

    @Autowired
    private com.pizzaria_system.repository.ProductRepository productRepository;

    @Autowired
    private ProductVariationRepository repository;

    public List<ProductVariationDto> findAll() {
        logger.info("Finding all ProductVariations");
        var ProductVariations = ObjectMapper.parseListObject(repository.findAll(), ProductVariationDto.class);
        ProductVariations.forEach(this::addHateoasLinks);
        return ProductVariations;
    }

    public ProductVariationDto findById(Long id) {
        logger.info("Finding one ProductVariation");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ProductVariation not found for this id"));
        var dto = ObjectMapper.parseObject(entity, ProductVariationDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ProductVariationDto create(ProductCreateVariationDto productVariationDto) {
        logger.info("Creating one ProductVariation");

        if(productVariationDto == null) throw new RequireObjectIsNullException("Payload is null");

        var entity = ObjectMapper.parseObject(productVariationDto, ProductVariation.class);
        if (productVariationDto.getProductId() != null) {
            var product = productRepository.findById(productVariationDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            entity.setProduct(product);
        } else {
            throw new RequireObjectIsNullException("ProductId is required for variation");
        }

        var savedEntity = repository.save(entity);

        ProductVariationDto dto = new ProductVariationDto();
        dto.setId(savedEntity.getId());
        dto.setSize(savedEntity.getSize());
        dto.setPrice(savedEntity.getPrice());

        return dto;
    }

    public ProductVariationDto update(ProductVariationDto ProductVariation) {
        logger.info("Updating all ProductVariations");

        if(ProductVariation == null) throw new RequireObjectIsNullException("Category not found");

        var entity = repository.findById(ProductVariation.getId()).orElseThrow(() -> new ResourceNotFoundException("ProductVariation not found for this id!"));
        entity.setComplements(ProductVariation.getComplements());
        entity.setFlavors(ProductVariation.getFlavors());
        entity.setPrice(ProductVariation.getPrice());
        entity.setSize(ProductVariation.getSize());
        entity.setStock(ProductVariation.getStock());
        entity.setProduct(ProductVariation.getProduct());
        var ProductVariationUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(ProductVariationUpdated, ProductVariationDto.class);
        addHateoasLinks(dto);
        return dto;

    }

    public void delete(Long id) {
        logger.info("Deleting one ProductVariation");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("ProductVariation not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(ProductVariationDto dto) {
        dto.add(linkTo(methodOn(ProductVariationController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ProductVariationController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ProductVariationController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ProductVariationController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
    
}

