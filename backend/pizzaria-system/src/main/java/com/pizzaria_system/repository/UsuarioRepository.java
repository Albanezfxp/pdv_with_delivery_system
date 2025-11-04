package com.pizzaria_system.repository;

import com.pizzaria_system.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Usuario  u set u.active = false where u.id = :id")
    void disableUser(@Param("id") Long id);
}
