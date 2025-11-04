package com.pizzaria_system.controller;


import com.pizzaria_system.data.dto.OrderItemDto;
import com.pizzaria_system.data.dto.OrderItemRequest;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.OrderEntity;
import com.pizzaria_system.model.TableEntity;
import com.pizzaria_system.repository.TableEntityRepository;
import com.pizzaria_system.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/by-table/{tableId}") // Removido "/order" do início
    public ResponseEntity<OrderEntity> getOrderByTable(@PathVariable Long tableId) {
        TableEntity table = tableEntityRepository.findByIdWithOrderAndItems(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada com ID: " + tableId));
        return ResponseEntity.ok(table.getOrder());
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

    @GetMapping("/itens-table/{order_id}") // Removido "/order" do início
    public ResponseEntity<List<OrderItemDto>> findByFkOderEntity(@PathVariable(value = "order_id") Long order_id) {
        // O código de retorno HTTP "CONTINUE" (100) não é apropriado para retorno de dados.
        // Use HttpStatus.OK (200) para sucesso.
        return new ResponseEntity<>(orderService.findByFkOderEntity(order_id), HttpStatus.OK);
    }
    // @GetMapping("/{orderId}") // Para buscar detalhes do pedido
    // @PostMapping("/close/{tableId}") // Para fechar o pedido da mesa

    @DeleteMapping("/itens-table/{product_id}")
    public void removeItemToTable(@PathVariable("product_id") Long product_id) {
        orderService.removeItemToTable(product_id);
    }
}