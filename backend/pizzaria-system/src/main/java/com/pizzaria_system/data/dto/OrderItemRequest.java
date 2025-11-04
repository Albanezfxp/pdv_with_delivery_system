package com.pizzaria_system.data.dto;

import java.util.List;

public class OrderItemRequest {

    private Long productVariationId;
    private Integer quantity;
    private List<Long> flavorIds;
    private List<Long> ComplementIds;
    // private String notes; // Opcional

    // --- Getters e Setters (Necessários) ---
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
        return ComplementIds;
    }

    public void setComplementIds(List<Long> complementIds) {
        ComplementIds = complementIds;
    }
}
