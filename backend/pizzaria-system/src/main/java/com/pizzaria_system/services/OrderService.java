package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.*;
import com.pizzaria_system.data.enums.OrderStatus;
import com.pizzaria_system.data.enums.Order_Type;
import com.pizzaria_system.data.enums.TableStatus;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.*;
import com.pizzaria_system.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderService {

    private final TableEntityRepository tableRepository;
    private final ProductVariationRepository variationRepository;
    private final FlavorRepository flavorRepository;
    private final ComplementRepository complementRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEntityRepository orderRepository;
    private final ClienteRepository clienteRepository;

    public OrderService(
            TableEntityRepository tableRepository,
            ProductVariationRepository variationRepository,
            FlavorRepository flavorRepository,
            ComplementRepository complementRepository,
            OrderItemRepository orderItemRepository,
            OrderEntityRepository orderRepository,
            ClienteRepository clienteRepository
    ) {
        this.tableRepository = tableRepository;
        this.variationRepository = variationRepository;
        this.flavorRepository = flavorRepository;
        this.complementRepository = complementRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.clienteRepository = clienteRepository;
    }

    public Page<OrderEntity> findAllOrdersForDelivery(Pageable pageable) {
        return orderRepository
                .findByType(Order_Type.DELIVERY, pageable);
    }

    public OrderEntityDto findOrderDeliveryById(Long id) {
        Stream<OrderEntity> order = orderRepository.findById(id).stream().filter(o -> o.getType() == Order_Type.DELIVERY);
        return ObjectMapper.parseObject(order, OrderEntityDto.class);
    }

    // --- Lógica de Busca de Itens por FK (GET /order/itens-table/{id}) ---
    public List<OrderItemDto> findByFkOderEntity(Long order_id) {
        List<OrderItem> itensEntities = orderItemRepository.findByOrder_Id(order_id);
        return itensEntities.stream().map(this::convertOrderItemToDto).collect(Collectors.toList());
    }



    private OrderItemDto convertOrderItemToDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());

        if (item.getProductVariation() != null && item.getProductVariation().getProduct() != null) {
            dto.setName(item.getProductVariation().getProduct().getName());
            dto.setSize(item.getProductVariation().getSize());
            dto.setProductVariationId(item.getProductVariation().getId());
        } else {
            dto.setName("Item Indisponível");
            dto.setSize("N/A");
        }

        dto.setNotes(item.getNotes());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        dto.setOrderId(item.getOrder().getId());

        if (item.getFlavors() != null) {
            dto.setFlavorIds(item.getFlavors().stream().map(Flavor::getId).toList());
        }
        if (item.getComplements() != null) {
            dto.setComplementIds(item.getComplements().stream().map(Complement::getId).toList());
        }

        return dto;
    }

    /**
     * ✅ DELIVERY (corrigido)
     *
     * PRINCIPAIS CORREÇÕES AQUI:
     * 1) DELIVERY não usa discount/addition -> seta ZERO (igual sua regra)
     * 2) NÃO faz newOrder.setItems(request.getItems()) porque isso causa:
     *    "detached entity passed to persist" (itens vindos do front são "soltos")
     *    -> aqui nós CRIAMOS e SALVAMOS os OrderItem corretamente.
     *
     * ⚠️ IMPORTANTE:
     * - Para funcionar 100%, o ideal é que OrderDeliveryDto.items seja:
     *      private List<OrderItemRequest> items;
     *   (igual o seu DTO OrderItemRequest)
     * - Se hoje está List<OrderItem>, mude para List<OrderItemRequest>.
     */
    @Transactional
    public ResponseEntity<OrderEntity> createOrderInDelivery(OrderDeliveryDto request) {

        // 1) salva cliente
        Cliente newCliente = new Cliente();
        newCliente.setName(request.getCliente_name());
        newCliente.setBirthday(request.getCliente_birthday());
        newCliente.setEndereco(request.getCliente_endereco());
        newCliente.setEmail(request.getCliente_email());
        newCliente.setPhone(request.getCliente_phone());

        Cliente savedCliente = clienteRepository.save(newCliente);

        // 2) cria order
        OrderEntity newOrder = new OrderEntity();
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setType(Order_Type.DELIVERY);

        // ✅ status do delivery (você usa PREPARING)
        newOrder.setStatus(OrderStatus.PREPARING);

        // ✅ DELIVERY não usa discount/addition
        newOrder.setDiscount(BigDecimal.ZERO);
        newOrder.setAddition(BigDecimal.ZERO);

        // subtotal/total
        BigDecimal subtotal = nvl(request.getSubtotal());
        newOrder.setSubtotal(subtotal);

        // se o front mandar total, usa; senão total = subtotal
        BigDecimal total = request.getTotal() != null ? request.getTotal() : subtotal;
        newOrder.setTotal(nvl(total));

        // pagamentos
        newOrder.setPayments(request.getPaymentEntries());

        // cliente
        newOrder.setClient(savedCliente);

        // salva order primeiro (precisa ter ID para relacionar itens)
        OrderEntity savedOrder = orderRepository.save(newOrder);

        // 3) salva itens (evita detached entity)
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Pedido delivery precisa ter pelo menos 1 item.");
        }

        List<OrderItem> savedItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            // busca variação
            ProductVariation variation = variationRepository.findById(itemReq.getProductVariationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variação não encontrada: " + itemReq.getProductVariationId()));

            // busca sabores/complementos
            List<Flavor> flavors = itemReq.getFlavorIds() != null
                    ? flavorRepository.findAllById(itemReq.getFlavorIds())
                    : List.of();

            List<Complement> complements = itemReq.getComplementIds() != null
                    ? complementRepository.findAllById(itemReq.getComplementIds())
                    : List.of();

            // cria item novo (do zero)
            OrderItem newItem = new OrderItem();
            newItem.setOrder(savedOrder);
            newItem.setProductVariation(variation);

            int qty = itemReq.getQuantity() != null ? itemReq.getQuantity() : 1;
            newItem.setQuantity(qty);

            newItem.setFlavors(flavors);
            newItem.setComplements(complements);

            // notes
            newItem.setNotes(buildNotes(variation, flavors, complements));

            // subtotal do item
            BigDecimal itemSubtotal = variation.getPrice().multiply(BigDecimal.valueOf(qty));
            newItem.setSubtotal(itemSubtotal);

            // persiste item
            OrderItem itemSaved = orderItemRepository.save(newItem);
            savedItems.add(itemSaved);
        }

        // 4) vincula itens no order e atualiza subtotal/total baseado no backend (opcional, mas recomendado)
        savedOrder.setItems(savedItems);

        BigDecimal backendSubtotal = savedItems.stream()
                .map(OrderItem::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        savedOrder.setSubtotal(backendSubtotal);

        // se você não tem frete no backend: total = subtotal
        savedOrder.setTotal(backendSubtotal);

        OrderEntity finalOrder = orderRepository.save(savedOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(finalOrder);
    }

    public void removeOrderInDelivery(Long id) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    public OrderEntityDto updateStatusOrderDelivery(OrderStatus status, Long id) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order não encontrada"));
        order.setStatus(status);
        orderRepository.save(order);

        return ObjectMapper.parseObject(order, OrderEntityDto.class);
    }

    public void removeItemToTable(Long productId) {
        var deleteProduct = orderItemRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de pedido não encontrado"));

        TableEntity table = deleteProduct.getOrder().getTable();
        OrderEntity order = deleteProduct.getOrder();

        orderItemRepository.delete(deleteProduct);

        if (orderItemRepository.countByOrderId(order.getId()) == 0) {
            table.setStatus(TableStatus.FREE);
            tableRepository.save(table);
        }
    }

    @Transactional
    public OrderItemDto addItemToTableOrder(Long tableId, OrderItemRequest request) {

        TableEntity table = tableRepository.findByIdWithOrderAndItems(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada com ID: " + tableId));

        table.setStatus(TableStatus.OCCUPIED);

        OrderEntity order = table.getOrder();
        if (order == null) {
            order = new OrderEntity();
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.OPEN); // ⚠️ se o CHECK no banco não permite OPEN, você precisa ajustar a constraint
            order.setTable(table);
            table.setOrder(order);
            order.setType(request.getType());
        }

        if (order.getType() == null) {
            order.setType(request.getType());
        }

        order = orderRepository.save(order);

        ProductVariation variation = variationRepository.findById(request.getProductVariationId())
                .orElseThrow(() -> new ResourceNotFoundException("Variação de Produto não encontrada"));

        List<Flavor> flavors = request.getFlavorIds() != null
                ? flavorRepository.findAllById(request.getFlavorIds())
                : List.of();

        List<Complement> complements = request.getComplementIds() != null
                ? complementRepository.findAllById(request.getComplementIds())
                : List.of();

        OrderItem newItem = new OrderItem();
        newItem.setOrder(order);
        newItem.setProductVariation(variation);
        newItem.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        newItem.setFlavors(flavors);
        newItem.setComplements(complements);

        newItem.setNotes(buildNotes(variation, flavors, complements));
        newItem.setSubtotal(variation.getPrice().multiply(BigDecimal.valueOf(newItem.getQuantity())));

        OrderItem savedItem = orderItemRepository.saveAndFlush(newItem);

        if (order.getItems() == null) order.setItems(new ArrayList<>());
        order.getItems().add(savedItem);

        tableRepository.save(table);

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
        dto.setSize(variation.getSize());

        return dto;
    }

    @Transactional
    public OrderPayRequestDto closeToTable(Long tableId, OrderPayRequestDto request) {

        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada com ID: " + tableId));

        OrderEntity order = table.getOrder();
        if (order == null) {
            throw new ResourceNotFoundException("Não há pedido ativo para a Mesa ID: " + tableId);
        }

        order.setPayments(request.getPaymentEntries());
        order.setAddition(request.getAddition());
        order.setDiscount(request.getDiscount());
        order.setTotal(request.getTotal());
        order.setStatus(OrderStatus.PAYED);
        order.setSubtotal(request.getSubtotal());

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            order.getItems().clear();
        }

        order.setTable(null);
        orderRepository.save(order);

        table.setOrder(null);
        if (table.getStatus() == TableStatus.OCCUPIED) {
            table.setStatus(TableStatus.FREE);
        }
        tableRepository.save(table);

        return request;
    }

    public List<OrderEntityDto> getAllOrdersWithPayments() {
        List<OrderEntity> orders = orderRepository.findAllWithPayments();
        return orders.stream().map(this::convertOrderEntityToDto).collect(Collectors.toList());
    }

    private OrderEntityDto convertOrderEntityToDto(OrderEntity order) {
        OrderEntityDto dto = new OrderEntityDto();
        dto.setId(order.getId());
        dto.setClient(order.getClient());
        dto.setUser(order.getUser());
        dto.setTable(order.getTable());
        dto.setPaymentMethods(order.getPayments());
        dto.setStatus(order.getStatus());
        dto.setDiscount(order.getDiscount());
        dto.setAddition(order.getAddition());
        dto.setTotal(order.getTotal());
        dto.setSubtotal(order.getSubtotal());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    // ----------------- helpers -----------------

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String buildNotes(ProductVariation variation, List<Flavor> flavors, List<Complement> complements) {
        String productName = variation.getProduct() != null ? variation.getProduct().getName() : "Produto";
        String size = variation.getSize() != null ? variation.getSize() : "";

        String notes = (productName + " " + size).trim();

        String flavorNames = (flavors == null ? List.<Flavor>of() : flavors)
                .stream().map(Flavor::getName).filter(Objects::nonNull).collect(Collectors.joining(", "));

        String complementNames = (complements == null ? List.<Complement>of() : complements)
                .stream().map(Complement::getName).filter(Objects::nonNull).collect(Collectors.joining(", "));

        if (!flavorNames.isEmpty()) notes += " (" + flavorNames + ")";
        if (!complementNames.isEmpty()) notes += " + " + complementNames;

        return notes;
    }
}