package com.pizzaria_system.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pizzaria_system.data.enums.Order_Type;

import java.util.List;

public class OrderItemRequest {

    private Long productVariationId;
    private Integer quantity;
    private List<Long> flavorIds;
    private List<Long> complementIds;
    // private String notes; // Opcional
    @JsonProperty("type")
    private Order_Type type;
    public Long getProductVariationId() {
        return productVariationId;
    }

    public void setProductVariationId(Long productVariationId) {
        this.productVariationId = productVariationId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<Long> getFlavorIds() {
        return flavorIds;
    }

    public void setFlavorIds(List<Long> flavorIds) {
        this.flavorIds = flavorIds;
    }

    public List<Long> getComplementIds() {
        return complementIds;
    }

    public void setComplementIds(List<Long> complementIds) {
        this.complementIds = complementIds;
    }

    public Order_Type getType() {
        return type;
    }

    public void setType(Order_Type type) {
        this.type = type;
    }
}
