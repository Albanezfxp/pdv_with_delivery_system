package com.pizzaria_system.controller;


import com.pizzaria_system.data.dto.*;
import com.pizzaria_system.data.enums.OrderStatus;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.OrderEntity;
import com.pizzaria_system.model.OrderItem;
import com.pizzaria_system.model.TableEntity;
import com.pizzaria_system.repository.TableEntityRepository;
import com.pizzaria_system.services.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final TableEntityRepository tableEntityRepository;

    // Injeção de dependência via construtor
    public OrderController(OrderService orderService, TableEntityRepository tableEntityRepository) {
        this.orderService = orderService;
        this.tableEntityRepository = tableEntityRepository;
    }

    @GetMapping("/orders-delivery")
    public ResponseEntity<Page<OrderEntity>> getAllOrdersFromDelivery(
            //ERRO: TAVA PASSANDO O PATHVARIABLE AO INVES DO REQUESTPARAM
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer page
    ) {
        //ERRO, TAVA PASSANDO PRIMEIRO O SIZE AO INVES DO PAGE
        Pageable pageable = PageRequest.of(page, size);
        return  ResponseEntity.ok(orderService.findAllOrdersForDelivery(pageable));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<OrderEntityDto>> getAllOdersWithPayments() {
        return ResponseEntity.ok(orderService.getAllOrdersWithPayments());
    }

    @PostMapping("/close/{tableId}")
    public ResponseEntity<OrderPayRequestDto> closeTableAndPay(
            @PathVariable Long tableId,
            @RequestBody OrderPayRequestDto request) {

        OrderPayRequestDto responseDto = orderService.closeToTable(tableId, request);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/by-table/{tableId}")
    public ResponseEntity<OrderEntity> getOrderByTable(@PathVariable Long tableId) {
        TableEntity table = tableEntityRepository.findByIdWithOrderAndItems(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada com ID: " + tableId));
        return ResponseEntity.ok(table.getOrder());
    }

    @GetMapping("/delivery_detail/{id}")
    public ResponseEntity<OrderEntityDto> findOrderDeliveryById(Long id) {
        return ResponseEntity.ok(orderService.findOrderDeliveryById(id));
    }

    @GetMapping("/itens-table/{order_id}") // Removido "/order" do início
    public ResponseEntity<List<OrderItemDto>> findByFkOderEntity(@PathVariable(value = "order_id") Long order_id) {
        // O código de retorno HTTP "CONTINUE" (100) não é apropriado para retorno de dados.
        // Use HttpStatus.OK (200) para sucesso.
        return new ResponseEntity<>(orderService.findByFkOderEntity(order_id), HttpStatus.OK);
    }

    /**
     * Rota para adicionar um novo item ao pedido ativo de uma mesa.
     * * Rota: POST http://localhost:8080/order/add-item/{tableId}
     * Body: OrderItemRequest (productVariationId, quantity, flavorIds)
     * * @param tableId O ID da mesa.
     * @param request O payload contendo os detalhes do item a ser adicionado.
     * @return O OrderItemDto criado, incluindo o ID gerado.
     */
    @PostMapping("/add-item/{tableId}")
    public ResponseEntity<OrderItemDto> addItemToTableOrder(
            @PathVariable(value = "tableId") Long tableId,
            @RequestBody OrderItemRequest request) {
        OrderItemDto savedItemDto = orderService.addItemToTableOrder(tableId, request);
        return new ResponseEntity<>(savedItemDto, HttpStatus.CREATED);
    }

    @PostMapping("/create_order_delivery")
    public ResponseEntity<OrderEntity> createOrderInDelivery(@RequestBody OrderDeliveryDto request) {
        // Extraia o corpo (body) do ResponseEntity retornado pelo Service
        OrderEntity savedOrder = orderService.createOrderInDelivery(request).getBody();

        // Retorne um novo ResponseEntity com o status correto (201 Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    // @GetMapping("/{orderId}") // Para buscar detalhes do pedido
    // @PostMapping("/close/{tableId}") // Para fechar o pedido da mesa

    @PatchMapping("/updatedStatus/{orderId}")
    public OrderEntityDto updateStatusDeliveryOrder(
            @PathVariable(value = "orderId") String id,
            @RequestBody OrderStatus status
            ) {
        Long realId = Long.parseLong(id);
        return orderService.updateStatusOrderDelivery(status, realId);
    }

    @DeleteMapping("/itens-table/{product_id}")
    public void removeItemToTable(@PathVariable("product_id") Long product_id) {
        orderService.removeItemToTable(product_id);
    }

    @DeleteMapping("/delete-order-delivery/{id}")
    public void removeOrderInDelivery(@PathVariable("id") Long id) {
        orderService.removeOrderInDelivery(id);
    }
}