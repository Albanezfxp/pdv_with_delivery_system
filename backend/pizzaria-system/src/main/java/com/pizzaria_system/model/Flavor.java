package com.pizzaria_system.model;

import com.pizzaria_system.data.enums.FlavorType;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "flavors")
public class Flavor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Enumerated(EnumType.STRING)
    private FlavorType type;

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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public FlavorType getType() {
        return type;
    }

    public void setType(FlavorType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Flavor flavor = (Flavor) o;
        return Objects.equals(id, flavor.id) && Objects.equals(name, flavor.name) && Objects.equals(description, flavor.description) && Objects.equals(product, flavor.product) && type == flavor.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, product, type);
    }
}
