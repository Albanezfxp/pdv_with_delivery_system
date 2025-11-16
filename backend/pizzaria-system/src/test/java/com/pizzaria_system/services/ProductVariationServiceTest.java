package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.ProductCreateVariationDto;
import com.pizzaria_system.data.dto.ProductVariationDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.Product;
import com.pizzaria_system.model.ProductVariation;
import com.pizzaria_system.repository.ProductVariationRepository;
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

class ProductVariationServiceTest {

    @Mock
    private ProductVariationRepository repository;

    @InjectMocks
    private ProductVariationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnListOfProductVariations() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza Calabresa");

        ProductVariation variation1 = new ProductVariation();
        variation1.setId(1L);
        variation1.setProduct(product);
        variation1.setSize("Grande");
        variation1.setPrice(BigDecimal.valueOf(50.00));
        variation1.setStock(10);

        ProductVariation variation2 = new ProductVariation();
        variation2.setId(2L);
        variation2.setProduct(product);
        variation2.setSize("Média");
        variation2.setPrice(BigDecimal.valueOf(40.00));
        variation2.setStock(8);

        when(repository.findAll()).thenReturn(List.of(variation1, variation2));

        List<ProductVariationDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Grande", result.get(0).getSize());
        assertEquals(BigDecimal.valueOf(50.00), result.get(0).getPrice());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoProductVariationsExist() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<ProductVariationDto> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnProductVariationWhenExists() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza Marguerita");

        ProductVariation entity = new ProductVariation();
        entity.setId(1L);
        entity.setProduct(product);
        entity.setSize("Grande");
        entity.setPrice(BigDecimal.valueOf(55.00));
        entity.setStock(5);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        ProductVariationDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Grande", result.getSize());
        assertEquals(BigDecimal.valueOf(55.00), result.getPrice());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void create_shouldSaveProductVariationSuccessfully() {
        ProductCreateVariationDto dto = new ProductCreateVariationDto();
        dto.setSize("Pequena");
        dto.setPrice(BigDecimal.valueOf(30.00));
        dto.setStock(20);

        ProductVariation entity = new ProductVariation();
        entity.setId(1L);
        entity.setSize("Pequena");
        entity.setPrice(BigDecimal.valueOf(30.00));
        entity.setStock(20);

        when(repository.save(any(ProductVariation.class))).thenReturn(entity);

        ProductVariationDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Pequena", result.getSize());
        assertEquals(BigDecimal.valueOf(30.00), result.getPrice());
        verify(repository, times(1)).save(any(ProductVariation.class));
    }

    @Test
    void create_shouldThrowExceptionWhenProductVariationIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldModifyProductVariationSuccessfully() {
        ProductVariation existing = new ProductVariation();
        existing.setId(1L);
        existing.setSize("Média");
        existing.setPrice(BigDecimal.valueOf(40.00));
        existing.setStock(5);

        ProductVariationDto updated = new ProductVariationDto();
        updated.setId(1L);
        updated.setSize("Grande");
        updated.setPrice(BigDecimal.valueOf(50.00));
        updated.setStock(8);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(ProductVariation.class))).thenReturn(existing);

        ProductVariationDto result = service.update(updated);

        assertNotNull(result);
        assertEquals("Grande", result.getSize());
        assertEquals(BigDecimal.valueOf(50.00), result.getPrice());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(ProductVariation.class));
    }

    @Test
    void update_shouldThrowExceptionWhenProductVariationIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.update(null));
        verify(repository, never()).save(any());
    }

    @Test
    void delete_shouldRemoveProductVariationSuccessfully() {
        ProductVariation entity = new ProductVariation();
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    void delete_shouldThrowExceptionWhenProductVariationNotFound() {
        Long nonExistentId = 999L;
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));
        verify(repository, never()).delete(any());
    }
}
