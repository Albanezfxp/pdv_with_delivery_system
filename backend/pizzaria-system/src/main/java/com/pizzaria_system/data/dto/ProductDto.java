package com.pizzaria_system.data.dto;

import com.pizzaria_system.model.Category;
import com.pizzaria_system.model.ProductVariation;
import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.Objects;

public class ProductDto extends RepresentationModel<ProductDto> {

    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Category category;
    private String imageUrl;
    private List<ProductVariationDto> variations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<ProductVariationDto> getVariations() {
        return variations;
    }

    public void setVariations(List<ProductVariationDto> variations) {
        this.variations = variations;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProductDto that = (ProductDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(active, that.active) && Objects.equals(category, that.category) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(variations, that.variations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, description, active, category, imageUrl, variations);
    }
}
