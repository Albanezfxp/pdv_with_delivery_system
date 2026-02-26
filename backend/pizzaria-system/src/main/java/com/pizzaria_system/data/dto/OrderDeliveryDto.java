package com.pizzaria_system.data.dto;

import com.pizzaria_system.data.enums.OrderStatus;
import com.pizzaria_system.data.enums.Order_Type;
import com.pizzaria_system.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderDeliveryDto {
    private Long cliente_id;
    private String cliente_name;
    private String cliente_phone;
    private String cliente_email;
    private LocalDate cliente_birthday;
    private Address cliente_endereco;
    private Long id;
    private Cliente client;
    private Usuario user;
    private TableEntity table;
    private Set<PaymentEntry> paymentMethods = new HashSet<>();
    private OrderStatus status;
    private BigDecimal discount;
    private BigDecimal addition;
    private BigDecimal total;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    private List<OrderItemRequest> items;
    private Order_Type type;

    public Long getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(Long cliente_id) {
        this.cliente_id = cliente_id;
    }

    public String getCliente_name() {
        return cliente_name;
    }

    public void setCliente_name(String cliente_name) {
        this.cliente_name = cliente_name;
    }

    public String getCliente_phone() {
        return cliente_phone;
    }

    public void setCliente_phone(String cliente_phone) {
        this.cliente_phone = cliente_phone;
    }

    public String getCliente_email() {
        return cliente_email;
    }

    public void setCliente_email(String cliente_email) {
        this.cliente_email = cliente_email;
    }

    public LocalDate getCliente_birthday() {
        return cliente_birthday;
    }

    public void setCliente_birthday(LocalDate cliente_birthday) {
        this.cliente_birthday = cliente_birthday;
    }

    public Address getCliente_endereco() {
        return cliente_endereco;
    }

    public void setCliente_endereco(Address cliente_endereco) {
        this.cliente_endereco = cliente_endereco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getClient() {
        return client;
    }

    public void setClient(Cliente client) {
        this.client = client;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public TableEntity getTable() {
        return table;
    }

    public void setTable(TableEntity table) {
        this.table = table;
    }

    public Set<PaymentEntry> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(Set<PaymentEntry> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getAddition() {
        return addition;
    }

    public void setAddition(BigDecimal addition) {
        this.addition = addition;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public Order_Type getType() {
        return type;
    }

    public void setType(Order_Type type) {
        this.type = type;
    }
}
