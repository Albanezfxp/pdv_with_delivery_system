package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.ClienteDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.Address;
import com.pizzaria_system.model.Cliente;
import com.pizzaria_system.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para ClienteService.
 * Verifica o comportamento dos métodos CRUD utilizando mocks do repositório.
 */
class ClienteServiceTest {

    // Mock: cria um repositório "falso" que não acessa banco de dados real.
    @Mock
    private ClienteRepository repository;

    // Injeta os mocks dentro do service real.
    @InjectMocks
    private ClienteService service;

    // Inicializa as anotações do Mockito antes de cada teste.
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Cria um endereço simulado (mock) para usar nos testes.
     */
    private Address createMockAddress() {
        Address address = new Address();
        address.setNickname("Casa");
        address.setCep("12345-678");
        address.setStreet("Rua das Flores");
        address.setNumber("100");
        address.setComplement("Apto 202");
        address.setReference("Próximo ao mercado");
        address.setCity("São Paulo");
        address.setState("SP");
        return address;
    }

    /**
     * Testa o método findAll() garantindo que retorna todos os clientes.
     */
    @Test
    void findAll_shouldReturnListOfClientes() {
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setName("Matheus");
        cliente1.setPhone("999999999");
        cliente1.setEmail("matheus@email.com");
        cliente1.setBirthday(LocalDate.of(2000, 5, 15));
        cliente1.setEndereco(createMockAddress());

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setName("Gabriel");
        cliente2.setPhone("888888888");
        cliente2.setEmail("gabriel@email.com");
        cliente2.setBirthday(LocalDate.of(1998, 3, 10));
        cliente2.setEndereco(createMockAddress());

        when(repository.findAll()).thenReturn(List.of(cliente1, cliente2));

        List<ClienteDto> resultDtos = service.findAll();

        assertNotNull(resultDtos);
        assertEquals(2, resultDtos.size());
        assertEquals("Matheus", resultDtos.get(0).getName());
        assertEquals("Gabriel", resultDtos.get(1).getName());

        verify(repository, times(1)).findAll();
    }

    /**
     * Testa o comportamento de findAll() quando o banco está vazio.
     */
    @Test
    void findAll_shouldReturnEmptyListWhenNoClientesFound() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<ClienteDto> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    /**
     * Testa o método findById() quando o cliente é encontrado.
     */
    @Test
    void findById_shouldReturnClienteDtoWhenFound() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setName("Matheus");
        cliente.setPhone("999999999");
        cliente.setEmail("matheus@email.com");
        cliente.setBirthday(LocalDate.of(2000, 5, 15));
        cliente.setEndereco(createMockAddress());

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Matheus", result.getName());
        assertEquals("999999999", result.getPhone());
        assertEquals("matheus@email.com", result.getEmail());

        verify(repository, times(1)).findById(1L);
    }

    /**
     * Testa findById() quando o cliente não é encontrado.
     */
    @Test
    void findById_shouldThrowExceptionWhenClienteNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));

        verify(repository, times(1)).findById(99L);
    }

    /**
     * Testa o método create() — deve salvar corretamente um novo cliente.
     */
    @Test
    void create_shouldSaveAndReturnClienteDto() {
        ClienteDto dto = new ClienteDto();
        dto.setName("Matheus");
        dto.setPhone("999999999");
        dto.setEmail("matheus@email.com");
        dto.setBirthday(LocalDate.of(2000, 5, 15));

        Cliente entity = new Cliente();
        entity.setId(1L);
        entity.setName("Matheus");
        entity.setPhone("999999999");
        entity.setEmail("matheus@email.com");
        entity.setBirthday(LocalDate.of(2000, 5, 15));
        entity.setEndereco(createMockAddress());

        when(repository.save(any(Cliente.class))).thenReturn(entity);

        ClienteDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Matheus", result.getName());
        verify(repository, times(1)).save(any(Cliente.class));
    }

    /**
     * Testa o comportamento de create() quando o DTO é nulo.
     */
    @Test
    void create_shouldThrowExceptionWhenDtoIsNull() {
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        verify(repository, never()).save(any());
    }

    /**
     * Testa o método update() — deve atualizar corretamente o cliente existente.
     */
    @Test
    void update_shouldUpdateExistingCliente() {
        Cliente existing = new Cliente();
        existing.setId(1L);
        existing.setName("Matheus");
        existing.setPhone("999999999");
        existing.setEmail("matheus@email.com");
        existing.setBirthday(LocalDate.of(2000, 5, 15));
        existing.setEndereco(createMockAddress());

        ClienteDto updatedDto = new ClienteDto();
        updatedDto.setId(1L);
        updatedDto.setName("Matheus Silva");
        updatedDto.setPhone("988888888");
        updatedDto.setEmail("matheus.silva@email.com");
        updatedDto.setBirthday(LocalDate.of(2000, 5, 15));

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Cliente.class))).thenReturn(existing);

        ClienteDto result = service.update(updatedDto);

        assertNotNull(result);
        assertEquals("Matheus Silva", result.getName());
        assertEquals("988888888", result.getPhone());
        verify(repository, times(1)).save(any(Cliente.class));
    }

    /**
     * Testa o método delete() — deve remover o cliente com ID existente.
     */
    @Test
    void delete_shouldRemoveClienteWhenFound() {
        Cliente entity = new Cliente();
        entity.setId(1L);
        entity.setName("Matheus");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    /**
     * Testa delete() quando o cliente não existe — deve lançar exceção.
     */
    @Test
    void delete_shouldThrowExceptionWhenClienteNotFound() {
        Long nonExistentId = 999L;

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));

        verify(repository, never()).delete(any());
    }
}
