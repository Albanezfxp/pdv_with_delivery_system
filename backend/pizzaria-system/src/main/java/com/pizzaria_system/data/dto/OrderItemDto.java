package com.pizzaria_system.data.dto;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class OrderItemDto extends RepresentationModel<OrderItemDto> {

    private Long id;
    private Long orderId;
    private String name;
    private String notes;
    private String size;
    private Integer quantity;
    private Long productVariationId;
    private List<Long> complementIds; // ✅ trocado para IDs
    private List<Long> flavorIds;
    private BigDecimal subtotal;

    // Getters e Setters


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Long getProductVariationId() { return productVariationId; }
    public void setProductVariationId(Long productVariationId) { this.productVariationId = productVariationId; }

    public List<Long> getFlavorIds() { return flavorIds; }
    public void setFlavorIds(List<Long> flavorIds) { this.flavorIds = flavorIds; }

    public List<Long> getComplementIds() { return complementIds; }
    public void setComplementIds(List<Long> complementIds) { this.complementIds = complementIds; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    // equals e hashCode atualizados
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItemDto that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(productVariationId, that.productVariationId) &&
                Objects.equals(flavorIds, that.flavorIds) &&
                Objects.equals(complementIds, that.complementIds) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(subtotal, that.subtotal) &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, productVariationId, flavorIds, complementIds, quantity, subtotal, notes);
    }
}
