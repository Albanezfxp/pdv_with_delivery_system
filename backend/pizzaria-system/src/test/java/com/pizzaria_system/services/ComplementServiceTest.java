package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.ComplementDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.Complement;
import com.pizzaria_system.repository.ComplementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ComplementServiceTest {

    @Mock
    private ComplementRepository repository;

    @InjectMocks
    private ComplementService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        Complement c1 = new Complement();
        c1.setId(1L);
        c1.setName("Borda recheada");
        c1.setPrice(new BigDecimal("5.00"));
        c1.setType("Adicional");

        Complement c2 = new Complement();
        c2.setId(2L);
        c2.setName("Molho extra");
        c2.setPrice(new BigDecimal("2.00"));
        c2.setType("Adicional");

        List<Complement> complements = List.of(c1, c2);

        ComplementDto dto1 = new ComplementDto();
        dto1.setId(1L);
        dto1.setName("Borda recheada");
        dto1.setPrice(new BigDecimal("5.00"));
        dto1.setType("Adicional");

        ComplementDto dto2 = new ComplementDto();
        dto2.setId(2L);
        dto2.setName("Molho extra");
        dto2.setPrice(new BigDecimal("2.00"));
        dto2.setType("Adicional");

        List<ComplementDto> expectedDtos = List.of(dto1, dto2);

        when(repository.findAll()).thenReturn(complements);

        List<ComplementDto> resultDtos = service.findAll();

        // Compara apenas os campos essenciais
        assertEquals(expectedDtos.size(), resultDtos.size());
        for (int i = 0; i < expectedDtos.size(); i++) {
            assertEquals(expectedDtos.get(i).getId(), resultDtos.get(i).getId());
            assertEquals(expectedDtos.get(i).getName(), resultDtos.get(i).getName());
            assertEquals(expectedDtos.get(i).getPrice(), resultDtos.get(i).getPrice());
            assertEquals(expectedDtos.get(i).getType(), resultDtos.get(i).getType());
        }

        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoComplementsAreFound() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        List<ComplementDto> result = service.findAll();
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void findById() {
        Complement entity = new Complement();
        entity.setId(1L);
        entity.setName("Borda recheada");
        entity.setPrice(new BigDecimal("5.00"));
        entity.setType("Adicional");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        ComplementDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Borda recheada", result.getName());
        assertEquals(new BigDecimal("5.00"), result.getPrice());
        assertEquals("Adicional", result.getType());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenComplementIsNotFoundById() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void create() {
        ComplementDto dto = new ComplementDto();
        dto.setName("Borda recheada");
        dto.setPrice(new BigDecimal("5.00"));
        dto.setType("Adicional");

        Complement entity = new Complement();
        entity.setId(1L);
        entity.setName("Borda recheada");
        entity.setPrice(new BigDecimal("5.00"));
        entity.setType("Adicional");

        when(repository.save(any(Complement.class))).thenReturn(entity);

        ComplementDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Borda recheada", result.getName());
        assertEquals(new BigDecimal("5.00"), result.getPrice());
        assertEquals("Adicional", result.getType());

        verify(repository, times(1)).save(any(Complement.class));
    }

    @Test
    void shouldThrowExceptionWhenComplementIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        verify(repository, never()).save(any());
    }

    @Test
    void update() {
        Complement existing = new Complement();
        existing.setId(1L);
        existing.setName("Borda recheada");
        existing.setPrice(new BigDecimal("5.00"));
        existing.setType("Adicional");

        ComplementDto updated = new ComplementDto();
        updated.setId(1L);
        updated.setName("Molho extra");
        updated.setPrice(new BigDecimal("2.00"));
        updated.setType("Adicional");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Complement.class))).thenReturn(existing);

        ComplementDto result = service.update(updated);

        assertNotNull(result);
        assertEquals("Molho extra", result.getName());
        assertEquals(new BigDecimal("2.00"), result.getPrice());
        assertEquals("Adicional", result.getType());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Complement.class));
    }

    @Test
    void delete() {
        Complement entity = new Complement();
        entity.setId(1L);
        entity.setName("Borda recheada");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    void shouldThrowExceptionWhenAttemptingToDeleteNonExistentComplement() {
        Long nonExistentId = 999L;

        doThrow(new ResourceNotFoundException("Complement not found"))
                .when(repository).deleteById(nonExistentId);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));
    }
}
