package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.*;
import com.pizzaria_system.data.enums.OrderStatus;
import com.pizzaria_system.data.enums.Order_Type;
import com.pizzaria_system.data.enums.PaymentMethod;
import com.pizzaria_system.data.enums.TableStatus;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.*;
import com.pizzaria_system.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private final OrderEntityRepository orderEntityRepository;

    public OrderService(
            TableEntityRepository tableRepository,
            ProductVariationRepository variationRepository,
            FlavorRepository flavorRepository,
            ComplementRepository complementRepository,
            OrderItemRepository orderItemRepository,
            OrderEntityRepository orderRepository, OrderEntityRepository odersRepository) {
        this.tableRepository = tableRepository;
        this.variationRepository = variationRepository;
        this.flavorRepository = flavorRepository;
        this.complementRepository = complementRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.orderEntityRepository = odersRepository;
    }
    @Autowired
    ClienteRepository clienteRepository;

    public Stream<OrderEntity> findAllOrdersForDelivery() {
        List<OrderEntity> orders = orderEntityRepository.findAll();
        return orders.stream().filter(o -> o.getType() == Order_Type.DELIVERY);
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

    public ResponseEntity<OrderEntity> createOrderInDelivery(OrderDeliveryDto request) {
        Cliente newCliente = new Cliente();
        newCliente.setName(request.getCliente_name());
        newCliente.setBirthday(request.getCliente_birthday());
        newCliente.setEndereco(request.getCliente_endereco());
        newCliente.setEmail(request.getCliente_email());
        newCliente.setPhone(request.getCliente_phone());

        Cliente savedCliente = clienteRepository.save(newCliente);

        OrderEntity newOrder = new OrderEntity();
        newOrder.setType(Order_Type.DELIVERY);
        newOrder.setSubtotal(request.getSubtotal());
        newOrder.setStatus(OrderStatus.PREPARING);
        newOrder.setDiscount(request.getDiscount());
        newOrder.setAddition(request.getAddition());
        newOrder.setTotal(request.getTotal());
        newOrder.setItems(request.getItems());
        newOrder.setPayments(request.getPaymentMethods());
        newOrder.setClient(savedCliente); // Vincula o cliente já salvo

        OrderEntity savedOrder = orderEntityRepository.save(newOrder);

        // 3. Retorno com Status 201 e o objeto criado no corpo
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    public void removeItemToTable(Long productId) {
        var deleteProduct = orderItemRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de pedido não encontrado"));

        // 1. Recupera a Mesa e o Pedido associados
        TableEntity table = deleteProduct.getOrder().getTable();
        OrderEntity order = deleteProduct.getOrder();

        // 2. Deleta o item do pedido
        orderItemRepository.delete(deleteProduct);

        // 3. Verifica se a lista de itens do pedido está vazia AGORA
        //    É necessário verificar no banco de dados ou em um objeto atualizado.
        //    Se Order for 'LAZY', uma nova chamada a getItems() pode funcionar,
        //    mas é mais seguro usar um COUNT ou RECARREGAR.

        // *Melhor forma: Usar um count no repository:*
        if (orderItemRepository.countByOrderId(order.getId()) == 0) {
            table.setStatus(TableStatus.FREE);
            // Salva a alteração de status da mesa
            tableRepository.save(table);
            // Obs: Se o pedido (Order) também for finalizado/removido, faça isso aqui.
        }
    }


    @Transactional
    public OrderItemDto addItemToTableOrder(Long tableId, OrderItemRequest request) {

        // Busca mesa e pedido associado
        TableEntity table = tableRepository.findByIdWithOrderAndItems(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada com ID: " + tableId));
         table.setStatus(TableStatus.OCCUPIED);

        OrderEntity order = table.getOrder();
        if (order == null) {
            order = new OrderEntity();
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.OPEN);
            order.setTable(table);
            table.setOrder(order);
            order.setType(request.getType());
        }

        if (order.getType() == null) {
            order.setType(request.getType());
        }

        order = orderRepository.save(order);

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
        dto.setSize(variation.getSize()); // <<-- SIZE ADICIONADO CORRETAMENTE AQUI

        return dto;
    }

    @Transactional
    public OrderPayRequestDto closeToTable(Long tableId, OrderPayRequestDto request) {

        // 1. Lógica de Mesa e Pedido
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada com ID: " + tableId));

        // ⚠️ MELHORIA: Verifique se há um pedido ativo antes de continuar
        OrderEntity order = table.getOrder();
        if (order == null) {
            throw new ResourceNotFoundException("Não há pedido ativo para a Mesa ID: " + tableId);
        }

        // 2. Aplica Pagamento e Salva Pedido
        order.setPayments(request.getPaymentEntries());
        order.setAddition(request.getAddition());
        order.setDiscount(request.getDiscount());
        order.setTotal(request.getTotal());
        order.setStatus(OrderStatus.PAYED);
        order.setSubtotal(request.getSubtotal());
        // Remove os itens do pedido ao finalizar pagamento para que a
        // consulta de itens retorne vazia (orphanRemoval remove os filhos)
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            order.getItems().clear();
        }

        // Desvincula a mesa e salva o pedido sem itens
        order.setTable(null);
        orderEntityRepository.save(order);

        table.setOrder(null); // Desvincula
        if (table.getStatus() == TableStatus.OCCUPIED) {
            table.setStatus(TableStatus.FREE);
        }
        tableRepository.save(table);

        return request;
    }

    public List<OrderEntityDto> getAllOrdersWithPayments() {
        List<OrderEntity> orders = orderEntityRepository.findAllWithPayments();

        // 🔹 CONVERSÃO MANUAL se o ObjectMapper não funcionar
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private OrderEntityDto convertToDto(OrderEntity order) {
        OrderEntityDto dto = new OrderEntityDto();
        dto.setId(order.getId());
        dto.setClient(order.getClient());
        dto.setUser(order.getUser());
        dto.setTable(order.getTable());
        dto.setPaymentMethods(order.getPayments()); // 🔹 GARANTIR que payments está aqui
        dto.setStatus(order.getStatus());
        dto.setDiscount(order.getDiscount());
        dto.setAddition(order.getAddition());
        dto.setTotal(order.getTotal());
        dto.setSubtotal(order.getSubtotal());
        dto.setCreatedAt(order.getCreatedAt());

        return dto;
    }
}