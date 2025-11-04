package com.pizzaria_system.repository;

import com.pizzaria_system.model.Category;
import com.pizzaria_system.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}
