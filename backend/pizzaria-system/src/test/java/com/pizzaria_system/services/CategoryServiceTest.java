package com.pizzaria_system.services;

import com.pizzaria_system.data.dto.CategoryDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.model.Category;
import com.pizzaria_system.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


class CategoryServiceTest {
    // Mock: cria uma versão "falsa" do repositório sem acessar banco de dados real.
    @Mock
    private CategoryRepository repository;

    // Injeta os mocks dentro da instância real de CategoryService.
    @InjectMocks
    private CategoryService service;

    // Método executado antes de cada teste — inicializa as anotações do Mockito.
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Pizzas");
        Category category2 = new Category();
        category2.setName("Bebidas");
        category2.setId(2L);
        List<Category> categories = List.of(category1, category2);

        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(1L);
        categoryDto1.setName("Pizzas");

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setName("Bebidas");

        List<CategoryDto> exprectDtos = List.of(categoryDto1, categoryDto2);

        when(repository.findAll()).thenReturn(categories);
        List<CategoryDto> resultDtos = service.findAll(); // já é List<CategoryDto>

        assertEquals(exprectDtos, resultDtos);

    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoCategoriesAreFound() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<CategoryDto> result = service.findAll();

        assertEquals(Collections.emptyList(), result);
    }

    /**
     * Testa o método findById(Long id) para garantir que busca corretamente uma categoria existente.
     */
    @Test
    void findById() {
        //Simula uma entidade existente no banco
        Category entity = new Category();
        entity.setId(1L);
        entity.setName("Pizzas");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        CategoryDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Pizzas", result.getName());

        verify(repository, times(1)).findById(1L);
    }

    /**
     * Testa o método findById(Long id) quando o ID não existe no banco.
     * Deve lançar ResourceNotFoundException.
     */
    @Test
    void ShouldThrowExceptionWhenCategoryIsNotFoundById() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository, times(1)).findById(99L);
    }

    /**
     * Testa o método create(CategoryDto).
     * Verifica se a categoria é criada corretamente e se o repositório é chamado.
     */
    @Test
    void create() {
        // Cria um DTO de exemplo (entrada simulada)
        CategoryDto category = new CategoryDto();
        category.setId(1L);
        category.setName("Pizzas");

        // Cria a entidade equivalente (resultado esperado do mapper)
        Category entity = new Category();
        entity.setId(1l);
        entity.setName("Pizzas");

        // Simula o comportamento do repository: quando salvar qualquer Category, retorna a entidade criada
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        // Executa o método que estamos testando
        CategoryDto result = service.create(category);

        // Verifica se o resultado não é nulo
        assertNotNull(result);

        // Verifica se o nome foi mantido corretamente
        assertEquals("Pizzas", result.getName());

        // Garante que o método save() do repositório foi chamado exatamente uma vez
        verify(repository, times(1)).save(any(Category.class));
    }

    /**
     * Testa o comportamento do método create(CategoryDto)
     * quando o DTO é nulo — deve lançar uma exceção personalizada.
     */
    @Test
    void shouldThrowExceptionWhenCategoryIsNull() {
        // Verifica se o método lança a exceção esperada
        assertThrows(RequireObjectIsNullException.class, () -> service.create(null));
        // Garante que o repositório não foi chamado
        verify(repository, never()).save(any());
    }

    @Test
    void update() {
        Category dto = new Category();
        dto.setName("Pizzas");
        dto.setId(1L);

        CategoryDto dtoUpdated = new CategoryDto();
        dtoUpdated.setName("Pasteis");
        dtoUpdated.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(dto));
        when(repository.save(any(Category.class))).thenReturn(dto);

        CategoryDto result = service.update(dtoUpdated);

        assertNotNull(result);

        assertEquals("Pasteis", result.getName());
        assertEquals(1L, result.getId());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Category.class));


    }

    @Test
    void delete() {
        Category entity = new Category();
        entity.setId(1L);
        entity.setName("Pizzas");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    void delete_shouldThrowExceptionWhenClienteNotFound() {
        Long nonExistentId = 999L;

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistentId));

        verify(repository, never()).delete(any());
    }

}
