package com.pizzaria_system.services;

import com.pizzaria_system.controller.OrderEntityController;
import com.pizzaria_system.data.dto.OrderEntityDto;
import com.pizzaria_system.data.dto.OrderItemDto; // Importação necessária
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.OrderEntity;
import com.pizzaria_system.model.OrderItem; // Importação necessária
import com.pizzaria_system.repository.OrderEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class OrderEntityService {
    private Logger logger = LoggerFactory.getLogger(OrderEntityService.class.getName());

    @Autowired
    private OrderEntityRepository repository;

    public List<OrderEntityDto> findAll() {
        logger.info("Finding all OrderEntitys");
        var OrderEntitys = ObjectMapper.parseListObject(repository.findAll(), OrderEntityDto.class);
        OrderEntitys.forEach(this::addHateoasLinks);
        return OrderEntitys;
    }

    public OrderEntityDto findById(Long id) {
        logger.info("Finding one OrderEntity");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("OrderEntity not found for this id"));
        var dto = ObjectMapper.parseObject(entity, OrderEntityDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public OrderEntityDto create(OrderEntityDto OrderEntity) {
        logger.info("Creating one OrderEntitys");

        if(OrderEntity == null) throw new RequireObjectIsNullException("OrderEntity object is null");

        var entity = ObjectMapper.parseObject(OrderEntity, OrderEntity.class);
        var createdEntity = repository.save(entity);
        var  dto = ObjectMapper.parseObject(createdEntity, OrderEntityDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public OrderEntityDto update(OrderEntityDto OrderEntity) {
        logger.info("Updating all OrderEntitys");

        if(OrderEntity == null) throw new RequireObjectIsNullException("OrderEntity object is null");

        var entity = repository.findById(OrderEntity.getId()).orElseThrow(() -> new ResourceNotFoundException("OrderEntity not found for this id!"));

        // Atualização dos campos simples
        entity.setAddition(OrderEntity.getAddition());
        entity.setClient(OrderEntity.getClient());
        entity.setDiscount(OrderEntity.getDiscount());
        entity.setTable(OrderEntity.getTable()); // Note: Se Table for um DTO aqui, isso falhará se não for mapeado.
        entity.setStatus(OrderEntity.getStatus());
        entity.setTotal(OrderEntity.getTotal());
        entity.setPayments(OrderEntity.getPaymentMethods());
        entity.setUser(OrderEntity.getUser());

        if (OrderEntity.getItems() != null) {
            List<OrderItem> itensEntity = ObjectMapper.parseListObject(
                    OrderEntity.getItems(),
                    OrderItem.class
            );
            entity.setItems(itensEntity);
        } else {
            entity.setItems(null);
        }

        var OrderEntityUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(OrderEntityUpdated, OrderEntityDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting one OrderEntity");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("OrderEntity not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(OrderEntityDto dto) {
        dto.add(linkTo(methodOn(OrderEntityController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(OrderEntityController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(OrderEntityController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(OrderEntityController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(OrderEntityController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}