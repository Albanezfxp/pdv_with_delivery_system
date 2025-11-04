package com.pizzaria_system.model;

import com.pizzaria_system.data.enums.TableStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "tables")
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TableStatus status;

    // 🔹 Espelha a relação OneToOne (não tem JoinColumn)
    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrderEntity order;

    public TableEntity() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { this.status = status; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }
}
