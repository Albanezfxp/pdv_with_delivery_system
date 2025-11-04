package com.pizzaria_system.repository;

import com.pizzaria_system.model.Flavor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlavorRepository extends JpaRepository<Flavor, Long> {
}
