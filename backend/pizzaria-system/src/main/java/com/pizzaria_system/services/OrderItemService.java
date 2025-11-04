package com.pizzaria_system.services;

import com.pizzaria_system.controller.OrderItemController;
import com.pizzaria_system.data.dto.ComplementDto; // Import necessário
import com.pizzaria_system.data.dto.OrderItemDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.OrderItem;
import com.pizzaria_system.repository.*; // Inclui todos os repositórios necessários
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors; // Para manipulação de Streams

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class OrderItemService {
    private Logger logger = LoggerFactory.getLogger(OrderItemService.class.getName());

    @Autowired
    private OrderItemRepository repository;

    // INJEÇÕES NECESSÁRIAS PARA BUSCAR RELACIONAMENTOS NO UPDATE
    @Autowired
    private OrderEntityRepository orderRepository;
    @Autowired
    private ComplementRepository complementRepository;
    @Autowired
    private ProductVariationRepository productVariationRepository;
    @Autowired
    private FlavorRepository flavorRepository;

    // Métodos find e delete (mantidos) ...

    public List<OrderItemDto> findAll() {
        logger.info("Finding all OrderItems");
        var OrderItems = ObjectMapper.parseListObject(repository.findAll(), OrderItemDto.class);
        OrderItems.forEach(this::addHateoasLinks);
        return OrderItems;
    }

    public OrderItemDto findById(Long id) {
        logger.info("Finding one OrderItem");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("OrderItem not found for this id"));
        var dto = ObjectMapper.parseObject(entity, OrderItemDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public OrderItemDto create(OrderItemDto OrderItem) {
        logger.info("Creating one OrderItems");

        // CORREÇÃO: Mensagem da exceção
        if(OrderItem == null) throw new RequireObjectIsNullException("OrderItem não pode ser nulo.");

        // NOTE: No CREATE, se OrderItemDto não contiver apenas IDs para relacionamentos,
        // o mapeador (Dozer) pode falhar aqui também. O ideal é ter um OrderItemRequest
        // simplificado para CREATE/UPDATE e o DTO completo para retorno (como foi feito no OrderService).

        var entity = ObjectMapper.parseObject(OrderItem, OrderItem.class);
        repository.save(entity);
        var  dto = ObjectMapper.parseObject(entity, OrderItemDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    // --- MÉTODO UPDATE CORRIGIDO ---
    public OrderItemDto update(OrderItemDto OrderItemDto) {
        logger.info("Updating all OrderItems");

        // CORREÇÃO: Mensagem da exceção e checagem de nulo
        if(OrderItemDto == null || OrderItemDto.getId() == null)
            throw new RequireObjectIsNullException("OrderItem ou ID não pode ser nulo para atualização.");

        var entity = repository.findById(OrderItemDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found for this id!"));

        // --- ATUALIZAÇÃO DOS RELACIONAMENTOS (APENAS POR ID) ---

        // 1. Pedido (Order) - Assume que OrderItemDto tem um getOrderId()
        if (OrderItemDto.getOrderId() != null) {
            var orderEntity = orderRepository.findById(OrderItemDto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido (Order) não encontrado."));
            entity.setOrder(orderEntity); // Recebe OrderEntity (Correto)
        }

        // 2. Variação do Produto (ProductVariation) - Assume que DTO tem getProductVariationId()
        if (OrderItemDto.getProductVariationId() != null) {
            var variation = productVariationRepository.findById(OrderItemDto.getProductVariationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variação de Produto não encontrada."));
            entity.setProductVariation(variation);
        }

        // 3. Sabores (Flavors) - Assume que DTO tem List<Long> flavorIds
        if (OrderItemDto.getFlavorIds() != null) {
            var flavors = flavorRepository.findAllById(OrderItemDto.getFlavorIds());
            entity.setFlavors(flavors);
        } else {
            entity.setFlavors(null);
        }

        // 4. Complementos (Complements) - Assume que DTO tem List<ComplementDto> e extraímos os IDs
        if (OrderItemDto.getComplementIds() != null && !OrderItemDto.getComplementIds().isEmpty()) {
            var complements = complementRepository.findAllById(OrderItemDto.getComplementIds());
            entity.setComplements(complements);
        } else {
            entity.setComplements(null); // ou List.of() se preferir lista vazia
        }
        // --- ATUALIZAÇÃO DE CAMPOS SIMPLES ---
        entity.setNotes(OrderItemDto.getNotes());
        entity.setQuantity(OrderItemDto.getQuantity());
        entity.setSubtotal(OrderItemDto.getSubtotal());

        var OrderItemUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(OrderItemUpdated, OrderItemDto.class);
        addHateoasLinks(dto);
        return dto;
    }
    // ... restante do serviço

    public void delete(Long id) {
        logger.info("Deleting one OrderItem");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("OrderItem not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(OrderItemDto dto) {
        dto.add(linkTo(methodOn(OrderItemController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(OrderItemController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(OrderItemController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(OrderItemController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(OrderItemController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}