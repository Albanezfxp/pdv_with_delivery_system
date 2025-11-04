package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.OrderItemDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.OrderItem;
import com.pizzaria_system.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderItemServiceTest {

    @Mock
    private OrderItemRepository repository;

    @InjectMocks
    private OrderItemService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnListOfItems() {
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setQuantity(2);
        item1.setSubtotal(new BigDecimal("50.00"));

        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        item2.setQuantity(1);
        item2.setSubtotal(new BigDecimal("30.00"));

        when(repository.findAll()).thenReturn(List.of(item1, item2));

        List<OrderItemDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoItemsExist() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        List<OrderItemDto> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnItemWhenExists() {
        OrderItem entity = new OrderItem();
        entity.setId(1L);
        entity.setQuantity(3);
        entity.setSubtotal(new BigDecimal("75.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        OrderItemDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(3, result.getQuantity());
        assertEquals(new BigDecimal("75.00"), result.getSubtotal());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void create_shouldSaveItemSuccessfully() {
        OrderItemDto dto = new OrderItemDto();
        dto.setQuantity(2);
        dto.setSubtotal(new BigDecimal("40.00"));
        dto.setNotes("Sem cebola");

        OrderItem entity = new OrderItem();
        entity.setId(1L);
        entity.setQuantity(2);
        entity.setSubtotal(new BigDecimal("40.00"));
        entity.setNotes("Sem cebola");

        when(repository.save(any(OrderItem.class))).thenReturn(entity);

        OrderItemDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(new BigDecimal("40.00"), result.getSubtotal());
        verify(repository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void create_shouldThrowExceptionWhenItemIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldModifyItemSuccessfully() {
        OrderItem existingItem = new OrderItem();
        existingItem.setId(1L);
        existingItem.setQuantity(1);
        existingItem.setSubtotal(new BigDecimal("25.00"));

        OrderItemDto updatedDto = new OrderItemDto();
        updatedDto.setId(1L);
        updatedDto.setQuantity(3);
        updatedDto.setSubtotal(new BigDecimal("75.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(repository.save(any(OrderItem.class))).thenReturn(existingItem);

        OrderItemDto result = service.update(updatedDto);

        assertNotNull(result);
        assertEquals(3, result.getQuantity());
        assertEquals(new BigDecimal("75.00"), result.getSubtotal());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void delete_shouldRemoveItemSuccessfully() {
        OrderItem entity = new OrderItem();
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    void delete_shouldThrowExceptionWhenItemNotFound() {
        Long nonExistentId = 999L;
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));
        verify(repository, never()).delete(any());
    }
}
