package com.pizzaria_system.repository;

import com.pizzaria_system.model.Complement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplementRepository extends JpaRepository<Complement, Long> {
}
