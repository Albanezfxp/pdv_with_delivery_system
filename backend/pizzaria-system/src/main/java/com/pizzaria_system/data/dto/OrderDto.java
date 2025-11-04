package com.pizzaria_system.data.dto;

import com.pizzaria_system.data.enums.PaymentMethod;
import com.pizzaria_system.model.Product;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class OrderDto extends RepresentationModel<OrderDto> {
    private Long id;
    private List<Product> products;
    private PaymentMethod paymentMethod;
    private BigDecimal subtotal;
    private LocalDate createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto order = (OrderDto) o;
        return Objects.equals(id, order.id) && Objects.equals(products, order.products) && Objects.equals(paymentMethod, order.paymentMethod) && Objects.equals(subtotal, order.subtotal) && Objects.equals(createdAt, order.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, products, paymentMethod, subtotal, createdAt);
    }
}
