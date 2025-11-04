package com.pizzaria_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Product product;

    private String size;
    private BigDecimal price;
    @Column(nullable = true)
    private Integer Stock;
    @Column(nullable = true)
    private Integer numberOfFlavor;
    @ManyToMany
    @JoinTable(name = "variation_flavors", joinColumns = @JoinColumn(name = "variation_id"), inverseJoinColumns = @JoinColumn(name = "flavor_id"))
    private List<Flavor> flavors;

    @ManyToMany
    @JoinTable(
            name = "variation_complements",
            joinColumns = @JoinColumn(name = "variation_id"),
            inverseJoinColumns = @JoinColumn(name = "complement_id")
    )
    private List<Complement> complements;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
        return Stock;
    }

    public void setStock(Integer stock) {
        Stock = stock;
    }

    public Integer getNumberOfFlavor() {
        return numberOfFlavor;
    }

    public void setNumberOfFlavor(Integer numberOfFlavor) {
        this.numberOfFlavor = numberOfFlavor;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariation that = (ProductVariation) o;
        return Objects.equals(id, that.id) && Objects.equals(product, that.product) && Objects.equals(size, that.size) && Objects.equals(price, that.price) && Objects.equals(Stock, that.Stock) && Objects.equals(numberOfFlavor, that.numberOfFlavor) && Objects.equals(flavors, that.flavors) && Objects.equals(complements, that.complements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, size, price, Stock, numberOfFlavor, flavors, complements);
    }
}
