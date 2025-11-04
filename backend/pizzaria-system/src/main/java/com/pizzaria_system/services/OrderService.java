package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.OrderItemDto;
import com.pizzaria_system.data.dto.OrderItemRequest;
import com.pizzaria_system.data.enums.OrderStatus;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.*;
import com.pizzaria_system.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final TableEntityRepository tableRepository;
    private final ProductVariationRepository variationRepository;
    private final FlavorRepository flavorRepository;
    private final ComplementRepository complementRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEntityRepository orderRepository;

    public OrderService(
            TableEntityRepository tableRepository,
            ProductVariationRepository variationRepository,
            FlavorRepository flavorRepository,
            ComplementRepository complementRepository,
            OrderItemRepository orderItemRepository,
            OrderEntityRepository orderRepository) {
        this.tableRepository = tableRepository;
        this.variationRepository = variationRepository;
        this.flavorRepository = flavorRepository;
        this.complementRepository = complementRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    // --- Lógica de Busca de Itens por FK (GET /order/itens-table/{id}) ---

    public List<OrderItemDto> findByFkOderEntity(Long order_id) {
        // Assume-se que o findByOrder_Id no repository está usando JOIN FETCH
        // para carregar ProductVariation e Product, evitando LazyInitializationException.
        List<OrderItem> itensEntities = orderItemRepository.findByOrder_Id(order_id);

        return itensEntities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Método auxiliar para conversão CORRIGIDO para incluir o Size
    private OrderItemDto convertToDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());

        // Garante que a ProductVariation existe antes de acessar o Produto e o Size
        if (item.getProductVariation() != null && item.getProductVariation().getProduct() != null) {
            dto.setName(item.getProductVariation().getProduct().getName());
            dto.setSize(item.getProductVariation().getSize()); // <<-- SIZE ADICIONADO
            dto.setProductVariationId(item.getProductVariation().getId());
        } else {
            // Fallback caso o produto/variação não exista (para evitar NPE)
            dto.setName("Item Indisponível");
            dto.setSize("N/A");
        }

        dto.setNotes(item.getNotes());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        dto.setOrderId(item.getOrder().getId());

        // Mapeamento de IDs de Sabor e Complemento (Assumindo que estão EAGER ou carregados)
        if (item.getFlavors() != null) {
            dto.setFlavorIds(item.getFlavors().stream().map(Flavor::getId).toList());
        }
        if (item.getComplements() != null) {
            dto.setComplementIds(item.getComplements().stream().map(Complement::getId).toList());
        }

        return dto;
    }

    public void removeItemToTable(Long productId) {
        var deleteProduct = orderItemRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        orderItemRepository.delete(deleteProduct);
    }

    // --- Lógica de Adicionar Item ao Pedido (POST /order/add-item/{tableId}) ---

    @Transactional
    public OrderItemDto addItemToTableOrder(Long tableId, OrderItemRequest request) {

        // Busca mesa e pedido associado
        TableEntity table = tableRepository.findByIdWithOrderAndItems(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada com ID: " + tableId));

        OrderEntity order = table.getOrder();
        if (order == null) {
            order = new OrderEntity();
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.OPEN);
            order.setTable(table);
            table.setOrder(order);
            order = orderRepository.save(order);
        }

        // Busca variação e relacionamentos
        ProductVariation variation = variationRepository.findById(request.getProductVariationId())
                .orElseThrow(() -> new ResourceNotFoundException("Variação de Produto não encontrada"));

        List<Flavor> flavors = request.getFlavorIds() != null
                ? flavorRepository.findAllById(request.getFlavorIds())
                : List.of();

        List<Complement> complements = request.getComplementIds() != null
                ? complementRepository.findAllById(request.getComplementIds())
                : List.of();

        // Cria item
        OrderItem newItem = new OrderItem();
        newItem.setOrder(order);
        newItem.setProductVariation(variation);
        newItem.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        newItem.setFlavors(flavors);
        newItem.setComplements(complements);

        // Monta observações (descrição) - CORRIGIDO PARA INCLUIR NOME/SIZE
        String flavorNames = flavors.stream().map(Flavor::getName).collect(Collectors.joining(", "));
        String complementNames = complements.stream().map(Complement::getName).collect(Collectors.joining(", "));

        // CORREÇÃO CRÍTICA: Inicializa com Nome do Produto e Size
        String notes = variation.getProduct().getName() + " " + variation.getSize();

        if (!flavorNames.isEmpty()) {
            notes += " (" + flavorNames + ")";
        }
        if (!complementNames.isEmpty()) {
            notes += " + " + complementNames;
        }

        newItem.setNotes(notes);
        // Subtotal
        newItem.setSubtotal(variation.getPrice().multiply(BigDecimal.valueOf(newItem.getQuantity())));

        // Salva item e garante ID
        OrderItem savedItem = orderItemRepository.saveAndFlush(newItem);

        // Atualiza lista de itens do pedido
        if (order.getItems() == null) order.setItems(new ArrayList<>());
        order.getItems().add(savedItem);

        // Atualiza mesa
        tableRepository.save(table);

        // Monta DTO de retorno (Corrigido para garantir o size)
        OrderItemDto dto = new OrderItemDto();
        dto.setId(savedItem.getId());
        dto.setName(savedItem.getProductVariation().getProduct().getName());
        dto.setNotes(savedItem.getNotes());
        dto.setQuantity(savedItem.getQuantity());
        dto.setSubtotal(savedItem.getSubtotal());
        dto.setOrderId(order.getId());
        dto.setProductVariationId(variation.getId());
        dto.setFlavorIds(flavors.stream().map(Flavor::getId).toList());
        dto.setComplementIds(complements.stream().map(Complement::getId).toList());
        dto.setSize(variation.getSize()); // <<-- SIZE ADICIONADO CORRETAMENTE AQUI

        return dto;
    }

}