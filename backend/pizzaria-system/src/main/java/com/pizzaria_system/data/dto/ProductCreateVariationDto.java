package com.pizzaria_system.data.dto;

import com.pizzaria_system.model.Complement;
import com.pizzaria_system.model.Flavor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ProductCreateVariationDto extends RepresentationModel<ProductCreateVariationDto> {

    private Long id;

    private Long productId; // Use apenas o ID em vez do objeto completo para evitar recursão
    private String size;
    private BigDecimal price;
    private Integer stock;

    private List<Flavor> flavors;

    private List<Complement> complements;
    private Integer numberOfFlavor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<Flavor> getFlavors() {
        return flavors;
    }

    public void setFlavors(List<Flavor> flavors) {
        this.flavors = flavors;
    }

    public List<Complement> getComplements() {
        return complements;
    }

    public void setComplements(List<Complement> complements) {
        this.complements = complements;
    }

    public Integer getNumberOfFlavor() {
        return numberOfFlavor;
    }

    public void setNumberOfFlavor(Integer numberOfFlavor) {
        this.numberOfFlavor = numberOfFlavor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProductCreateVariationDto that = (ProductCreateVariationDto) o;
        return Objects.equals(id, that.id) && Objects.equals(productId, that.productId) && Objects.equals(size, that.size) && Objects.equals(price, that.price) && Objects.equals(stock, that.stock) && Objects.equals(flavors, that.flavors) && Objects.equals(complements, that.complements) && Objects.equals(numberOfFlavor, that.numberOfFlavor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, productId, size, price, stock, flavors, complements, numberOfFlavor);
    }
}
