package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.FlavorDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.Flavor;
import com.pizzaria_system.repository.FlavorRepository;
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

class FlavorServiceTest {

    @Mock
    private FlavorRepository repository;

    @InjectMocks
    private FlavorService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnListOfFlavors() {
        Flavor flavor1 = new Flavor();
        flavor1.setId(1L);
        flavor1.setName("Calabresa");
        flavor1.setDescription("Molho, mussarela e calabresa");

        Flavor flavor2 = new Flavor();
        flavor2.setId(2L);
        flavor2.setName("Frango com Catupiry");
        flavor2.setDescription("Molho, frango desfiado e catupiry");

        List<Flavor> flavors = List.of(flavor1, flavor2);

        when(repository.findAll()).thenReturn(flavors);

        List<FlavorDto> resultDtos = service.findAll();

        assertNotNull(resultDtos);
        assertEquals(2, resultDtos.size());
        assertEquals("Calabresa", resultDtos.get(0).getName());
        assertEquals("Frango com Catupiry", resultDtos.get(1).getName());

        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoFlavorsAreFound() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<FlavorDto> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnFlavorWhenExists() {
        Flavor entity = new Flavor();
        entity.setId(1L);
        entity.setName("Calabresa");
        entity.setDescription("Molho, mussarela e calabresa");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        FlavorDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Calabresa", result.getName());
        assertEquals("Molho, mussarela e calabresa", result.getDescription());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void create_shouldSaveFlavorSuccessfully() {
        FlavorDto dto = new FlavorDto();
        dto.setName("Calabresa");
        dto.setDescription("Molho, mussarela e calabresa");

        Flavor entity = new Flavor();
        entity.setId(1L);
        entity.setName("Calabresa");
        entity.setDescription("Molho, mussarela e calabresa");

        when(repository.save(any(Flavor.class))).thenReturn(entity);

        FlavorDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Calabresa", result.getName());
        assertEquals("Molho, mussarela e calabresa", result.getDescription());
        verify(repository, times(1)).save(any(Flavor.class));
    }

    @Test
    void create_shouldThrowExceptionWhenFlavorIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldModifyFlavorSuccessfully() {
        Flavor existingFlavor = new Flavor();
        existingFlavor.setId(1L);
        existingFlavor.setName("Calabresa");
        existingFlavor.setDescription("Molho e calabresa");

        FlavorDto updatedDto = new FlavorDto();
        updatedDto.setId(1L);
        updatedDto.setName("Frango com Catupiry");
        updatedDto.setDescription("Molho, frango desfiado e catupiry");

        when(repository.findById(1L)).thenReturn(Optional.of(existingFlavor));
        when(repository.save(any(Flavor.class))).thenReturn(existingFlavor);

        FlavorDto result = service.update(updatedDto);

        assertNotNull(result);
        assertEquals("Frango com Catupiry", result.getName());
        assertEquals("Molho, frango desfiado e catupiry", result.getDescription());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Flavor.class));
    }

    @Test
    void delete_shouldRemoveFlavorSuccessfully() {
        Flavor entity = new Flavor();
        entity.setId(1L);
        entity.setName("Calabresa");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    void delete_shouldThrowExceptionWhenFlavorNotFound() {
        Long nonExistentId = 999L;

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));
        verify(repository, never()).delete(any());
    }
}
