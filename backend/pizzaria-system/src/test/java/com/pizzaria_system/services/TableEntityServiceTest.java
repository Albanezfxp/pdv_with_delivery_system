package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.TableEntityDto;
import com.pizzaria_system.data.enums.TableStatus;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.TableEntity;
import com.pizzaria_system.repository.TableEntityRepository;
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

class TableEntityServiceTest {

    @Mock
    private TableEntityRepository repository;

    @InjectMocks
    private TableEntityService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnListOfTables() {
        TableEntity table1 = new TableEntity();
        table1.setId(1L);
        table1.setName("01");
        table1.setStatus(TableStatus.FREE);

        TableEntity table2 = new TableEntity();
        table2.setId(2L);
        table2.setName("02");
        table2.setStatus(TableStatus.OCCUPIED);

        when(repository.findAll()).thenReturn(List.of(table1, table2));

        List<TableEntityDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("01", result.get(0).getName());
        assertEquals(TableStatus.FREE, result.get(0).getStatus());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoTablesExist() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<TableEntityDto> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnTableWhenExists() {
        TableEntity entity = new TableEntity();
        entity.setId(1L);
        entity.setName("05");
        entity.setStatus(TableStatus.RESERVED);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        TableEntityDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("05", result.getName());
        assertEquals(TableStatus.RESERVED, result.getStatus());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void create_shouldSaveTableSuccessfully() {
        TableEntityDto dto = new TableEntityDto();
        dto.setName("07");
        dto.setStatus(TableStatus.FREE);

        TableEntity entity = new TableEntity();
        entity.setId(1L);
        entity.setName("07");
        entity.setStatus(TableStatus.FREE);

        when(repository.save(any(TableEntity.class))).thenReturn(entity);

        TableEntityDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("07", result.getName());
        assertEquals(TableStatus.FREE, result.getStatus());
        verify(repository, times(1)).save(any(TableEntity.class));
    }

    @Test
    void create_shouldThrowExceptionWhenTableIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldModifyTableSuccessfully() {
        TableEntity existing = new TableEntity();
        existing.setId(1L);
        existing.setName("08");
        existing.setStatus(TableStatus.FREE);

        TableEntityDto updated = new TableEntityDto();
        updated.setId(1L);
        updated.setName("08");
        updated.setStatus(TableStatus.OCCUPIED);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(TableEntity.class))).thenReturn(existing);

        TableEntityDto result = service.update(updated);

        assertNotNull(result);
        assertEquals("08", result.getName());
        assertEquals(TableStatus.OCCUPIED, result.getStatus());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(TableEntity.class));
    }

    @Test
    void update_shouldThrowExceptionWhenTableIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.update(null));
        verify(repository, never()).save(any());
    }

    @Test
    void delete_shouldRemoveTableSuccessfully() {
        TableEntity entity = new TableEntity();
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    void delete_shouldThrowExceptionWhenTableNotFound() {
        Long nonExistentId = 999L;
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));
        verify(repository, never()).delete(any());
    }
}
