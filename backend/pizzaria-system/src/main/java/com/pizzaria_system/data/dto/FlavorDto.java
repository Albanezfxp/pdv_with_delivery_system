package com.pizzaria_system.data.dto;

import com.pizzaria_system.data.enums.FlavorType;
import com.pizzaria_system.model.Product;
import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class FlavorDto extends RepresentationModel<FlavorDto> {

    private Long id;

    private String name;
    private String description;
    private FlavorType type;

    public FlavorType getType() {
        return type;
    }

    public void setType(FlavorType type) {
        this.type = type;
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FlavorDto flavor = (FlavorDto) o;
        return Objects.equals(id, flavor.id) && Objects.equals(name, flavor.name) && Objects.equals(description, flavor.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }


}
