package com.pizzaria_system.repository;

import com.pizzaria_system.model.Cliente;
import com.pizzaria_system.model.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {

}
