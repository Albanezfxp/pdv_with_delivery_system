package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.OrderEntityDto;
import com.pizzaria_system.data.enums.OrderStatus;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.OrderEntity;
import com.pizzaria_system.repository.OrderEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderEntityServiceTest {

    @Mock
    private OrderEntityRepository repository;

    @InjectMocks
    private OrderEntityService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnListOfOrders() {
        OrderEntity order1 = new OrderEntity();
        order1.setId(1L);
        order1.setStatus(OrderStatus.OPEN);
        order1.setTotal(new BigDecimal("80.00"));
        order1.setCreatedAt(LocalDateTime.now());

        OrderEntity order2 = new OrderEntity();
        order2.setId(2L);
        order2.setStatus(OrderStatus.COMPLETED);
        order2.setTotal(new BigDecimal("150.00"));
        order2.setCreatedAt(LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(order1, order2));

        List<OrderEntityDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(OrderStatus.OPEN, result.get(0).getStatus());
        assertEquals(OrderStatus.COMPLETED, result.get(1).getStatus());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoOrdersExist() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        List<OrderEntityDto> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnOrderWhenExists() {
        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setStatus(OrderStatus.OPEN);
        entity.setTotal(new BigDecimal("90.00"));
        entity.setCreatedAt(LocalDateTime.now());

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        OrderEntityDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.OPEN, result.getStatus());
        assertEquals(new BigDecimal("90.00"), result.getTotal());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void create_shouldSaveOrderSuccessfully() {
        OrderEntityDto dto = new OrderEntityDto();
        dto.setStatus(OrderStatus.OPEN);
        dto.setTotal(new BigDecimal("120.00"));
        dto.setDiscount(BigDecimal.ZERO);
        dto.setAddition(BigDecimal.ZERO);

        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setStatus(OrderStatus.OPEN);
        entity.setTotal(new BigDecimal("120.00"));

        when(repository.save(any(OrderEntity.class))).thenReturn(entity);

        OrderEntityDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(OrderStatus.OPEN, result.getStatus());
        assertEquals(new BigDecimal("120.00"), result.getTotal());
        verify(repository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void create_shouldThrowExceptionWhenOrderIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldModifyOrderSuccessfully() {
        OrderEntity existingOrder = new OrderEntity();
        existingOrder.setId(1L);
        existingOrder.setStatus(OrderStatus.OPEN);
        existingOrder.setTotal(new BigDecimal("100.00"));

        OrderEntityDto updatedDto = new OrderEntityDto();
        updatedDto.setId(1L);
        updatedDto.setStatus(OrderStatus.COMPLETED);
        updatedDto.setTotal(new BigDecimal("110.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(repository.save(any(OrderEntity.class))).thenReturn(existingOrder);

        OrderEntityDto result = service.update(updatedDto);

        assertNotNull(result);
        assertEquals(OrderStatus.COMPLETED, result.getStatus());
        assertEquals(new BigDecimal("110.00"), result.getTotal());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void delete_shouldRemoveOrderSuccessfully() {
        OrderEntity entity = new OrderEntity();
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    void delete_shouldThrowExceptionWhenOrderNotFound() {
        Long nonExistentId = 999L;
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));
        verify(repository, never()).delete(any());
    }
}
