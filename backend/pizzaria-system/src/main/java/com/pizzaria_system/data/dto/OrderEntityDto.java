package com.pizzaria_system.data.dto;

import com.pizzaria_system.data.enums.OrderStatus;
import com.pizzaria_system.data.enums.PaymentMethod;
import com.pizzaria_system.model.Cliente;
import com.pizzaria_system.model.OrderItem;
import com.pizzaria_system.model.TableEntity;
import com.pizzaria_system.model.Usuario;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderEntityDto extends RepresentationModel<OrderEntityDto> {

    private Long id;

    private Cliente client;

    private Usuario user;

    private TableEntity table;

    private List<PaymentEntry> paymentMethods = new ArrayList<>();
    private OrderStatus status;

    private BigDecimal discount;
    private BigDecimal addition;
    private BigDecimal total;

    private LocalDateTime createdAt;

    private List<OrderItemDto> items;
    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getClient() { return client; }
    public void setClient(Cliente client) { this.client = client; }

    public Usuario getUser() { return user; }
    public void setUser(Usuario user) { this.user = user; }

    public TableEntity getTable() { return table; }
    public void setTable(TableEntity table) { this.table = table; }

    public List<PaymentEntry> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentEntry> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getAddition() { return addition; }
    public void setAddition(BigDecimal addition) { this.addition = addition; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}
