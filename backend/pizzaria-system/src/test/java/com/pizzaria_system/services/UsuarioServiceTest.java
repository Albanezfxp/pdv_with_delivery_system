package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.UsuarioDto;
import com.pizzaria_system.data.enums.RoleEnum;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.Usuario;
import com.pizzaria_system.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnListOfUsuarios() {
        Usuario user1 = new Usuario();
        user1.setId(1L);
        user1.setName("Gabriel");
        user1.setEmail("gabriel@email.com");
        user1.setRole(RoleEnum.ADMIN);
        user1.setActive(true);

        Usuario user2 = new Usuario();
        user2.setId(2L);
        user2.setName("Maria");
        user2.setEmail("maria@email.com");
        user2.setRole(RoleEnum.ATENDENTE);
        user2.setActive(true);

        when(repository.findAll()).thenReturn(List.of(user1, user2));

        List<UsuarioDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Gabriel", result.get(0).getName());
        assertEquals(RoleEnum.ADMIN, result.get(0).getRole());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoUsuariosExist() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<UsuarioDto> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnUsuarioWhenExists() {
        Usuario user = new Usuario();
        user.setId(1L);
        user.setName("Ana");
        user.setEmail("ana@email.com");
        user.setRole(RoleEnum.ATENDENTE);
        user.setActive(true);

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UsuarioDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ana", result.getName());
        assertEquals(RoleEnum.ATENDENTE, result.getRole());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void create_shouldSaveUsuarioSuccessfully() {
        UsuarioDto dto = new UsuarioDto();
        dto.setName("Lucas");
        dto.setEmail("lucas@email.com");
        dto.setRole(RoleEnum.ATENDENTE);
        dto.setActive(true);

        Usuario entity = new Usuario();
        entity.setId(1L);
        entity.setName("Lucas");
        entity.setEmail("lucas@email.com");
        entity.setRole(RoleEnum.ATENDENTE);
        entity.setActive(true);

        when(repository.save(any(Usuario.class))).thenReturn(entity);

        UsuarioDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Lucas", result.getName());
        assertEquals(RoleEnum.ATENDENTE, result.getRole());
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    void create_shouldThrowExceptionWhenUsuarioIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldModifyUsuarioSuccessfully() {
        Usuario existing = new Usuario();
        existing.setId(1L);
        existing.setName("João");
        existing.setEmail("joao@email.com");
        existing.setRole(RoleEnum.ATENDENTE);
        existing.setActive(true);

        UsuarioDto updated = new UsuarioDto();
        updated.setId(1L);
        updated.setName("João da Silva");
        updated.setEmail("joao@email.com");
        updated.setRole(RoleEnum.ADMIN);
        updated.setActive(true);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Usuario.class))).thenReturn(existing);

        UsuarioDto result = service.update(updated);

        assertNotNull(result);
        assertEquals("João da Silva", result.getName());
        assertEquals(RoleEnum.ADMIN, result.getRole());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    void update_shouldThrowExceptionWhenUsuarioIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.update(null));
        verify(repository, never()).save(any());
    }

    @Test
    void delete_shouldRemoveUsuarioSuccessfully() {
        Usuario user = new Usuario();
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1L);

        verify(repository, times(1)).delete(user);
    }

    @Test
    void delete_shouldThrowExceptionWhenUsuarioNotFound() {
        Long nonExistentId = 999L;
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));
        verify(repository, never()).delete(any());
    }
}
