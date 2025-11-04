package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.ProductDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.Category;
import com.pizzaria_system.model.Product;
import com.pizzaria_system.repository.ProductRepository;
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

class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnListOfProducts() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Pizza Calabresa");
        product1.setDescription("Pizza sabor calabresa com queijo e cebola");
        product1.setActive(true);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Refrigerante Coca-Cola");
        product2.setDescription("Lata 350ml");
        product2.setActive(true);

        when(repository.findAll()).thenReturn(List.of(product1, product2));

        List<ProductDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pizza Calabresa", result.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoProductsExist() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<ProductDto> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnProductWhenExists() {
        Product entity = new Product();
        entity.setId(1L);
        entity.setName("Pizza Marguerita");
        entity.setDescription("Queijo, tomate e manjericão");
        entity.setActive(true);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        ProductDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pizza Marguerita", result.getName());
        assertEquals("Queijo, tomate e manjericão", result.getDescription());
        assertTrue(result.getActive());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void create_shouldSaveProductSuccessfully() {
        ProductDto dto = new ProductDto();
        dto.setName("Pizza Portuguesa");
        dto.setDescription("Presunto, ovos e cebola");
        dto.setActive(true);

        Product entity = new Product();
        entity.setId(1L);
        entity.setName("Pizza Portuguesa");
        entity.setDescription("Presunto, ovos e cebola");
        entity.setActive(true);

        when(repository.save(any(Product.class))).thenReturn(entity);

        ProductDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Pizza Portuguesa", result.getName());
        assertTrue(result.getActive());
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void create_shouldThrowExceptionWhenProductIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldModifyProductSuccessfully() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Pizza 4 Queijos");
        existingProduct.setDescription("Mussarela, provolone, parmesão e gorgonzola");
        existingProduct.setActive(true);

        ProductDto updatedDto = new ProductDto();
        updatedDto.setId(1L);
        updatedDto.setName("Pizza de Frango com Catupiry");
        updatedDto.setDescription("Frango desfiado e catupiry");
        updatedDto.setActive(true);

        when(repository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(repository.save(any(Product.class))).thenReturn(existingProduct);

        ProductDto result = service.update(updatedDto);

        assertNotNull(result);
        assertEquals("Pizza de Frango com Catupiry", result.getName());
        assertTrue(result.getActive());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void update_shouldThrowExceptionWhenProductIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.update(null));
        verify(repository, never()).save(any());
    }

    @Test
    void delete_shouldRemoveProductSuccessfully() {
        Product entity = new Product();
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    void delete_shouldThrowExceptionWhenProductNotFound() {
        Long nonExistentId = 999L;
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));
        verify(repository, never()).delete(any());
    }
}
