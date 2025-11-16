package com.pizzaria_system.model;

import com.pizzaria_system.data.dto.PaymentEntry;
import com.pizzaria_system.data.enums.OrderStatus;
import com.pizzaria_system.data.enums.Order_Type;
import com.pizzaria_system.data.enums.PaymentMethod;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders") // é bom dar nome explícito
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Cliente client;

    @ManyToOne
    private Usuario user;

    // 🔹 Dono da relação OneToOne (tem a FK table_id)
    @OneToOne
    @JoinColumn(name = "table_id", unique = true)
    private TableEntity table;

    // Mapeia a lista da classe embutível
    @ElementCollection
    @CollectionTable(
            name = "order_payments_made", // Nome da nova tabela de registro de pagamentos
            joinColumns = @JoinColumn(name = "order_id")
    )
    private Set<PaymentEntry> payments = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal discount;
    private BigDecimal addition;
    private BigDecimal total;
    private BigDecimal subtotal;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Order_Type type;

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getClient() { return client; }
    public void setClient(Cliente client) { this.client = client; }

    public Usuario getUser() { return user; }
    public void setUser(Usuario user) { this.user = user; }

    public TableEntity getTable() { return table; }
    public void setTable(TableEntity table) { this.table = table; }

    public Set<PaymentEntry> getPayments() {
        return payments;
    }

    public void setPayments(Set<PaymentEntry> payments) {
        this.payments = payments;
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

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Order_Type getType() {
        return type;
    }

    public void setType(Order_Type type) {
        this.type = type;
    }
}
