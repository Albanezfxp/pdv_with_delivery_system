package com.pizzaria_system.data.dto;

import com.pizzaria_system.data.enums.TableStatus;
import com.pizzaria_system.model.OrderEntity;
import org.springframework.hateoas.RepresentationModel;

public class TableEntityDto extends RepresentationModel<TableEntityDto> {

    private Long id;

    private String name;

    private TableStatus status;

    private OrderDto order;
    public TableEntityDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { this.status = status; }

    public OrderDto getOrder() { return order; }
    public void setOrder(OrderDto order) { this.order = order; }
}
