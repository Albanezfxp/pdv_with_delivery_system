package com.pizzaria_system.services;

import com.pizzaria_system.controller.TableEntityController;
import com.pizzaria_system.data.dto.TableEntityDto;
import com.pizzaria_system.exception.RequireObjectIsNullException;
import com.pizzaria_system.exception.ResourceNotFoundException;
import com.pizzaria_system.mapper.ObjectMapper;
import com.pizzaria_system.model.TableEntity;
import com.pizzaria_system.repository.TableEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TableEntityService {
        private Logger logger = LoggerFactory.getLogger(TableEntityService.class.getName());

    @Autowired
    private TableEntityRepository repository;

    public List<TableEntityDto> findAll() {
        logger.info("Finding all TableEntitys");
        var TableEntitys = ObjectMapper.parseListObject(repository.findAll(Sort.by("id").ascending()), TableEntityDto.class);
        TableEntitys.forEach(this::addHateoasLinks);
        return TableEntitys;
    }

    public TableEntityDto findById(Long id) {
        logger.info("Finding one TableEntity");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TableEntity not found for this id"));
        var dto = ObjectMapper.parseObject(entity, TableEntityDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public TableEntityDto create(TableEntityDto TableEntity) {
        logger.info("Creating one TableEntitys");

        if(TableEntity == null) throw new RequireObjectIsNullException("Category not found");

        var entity = ObjectMapper.parseObject(TableEntity, TableEntity.class);
        repository.save(entity);
        var  dto = ObjectMapper.parseObject(entity, TableEntityDto.class);
        addHateoasLinks(dto);
        return dto;
    }
    public TableEntityDto update(TableEntityDto TableEntity) {
        logger.info("Updating all TableEntitys");

        if(TableEntity == null) throw new RequireObjectIsNullException("Category not found");

        var entity = repository.findById(TableEntity.getId()).orElseThrow(() -> new ResourceNotFoundException("TableEntity not found for this id!"));
        entity.setName(TableEntity.getName());
        entity.setStatus(TableEntity.getStatus());

        var TableEntityUpdated = repository.save(entity);

        var dto = ObjectMapper.parseObject(TableEntityUpdated, TableEntityDto.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ResponseEntity<TableEntity> tableEditName(Long id, TableEntity table) {
        Optional<TableEntity> optionalTable = repository.findById(id);

        // CORREÇÃO: Verifica se o recurso NÃO foi encontrado.
        if (optionalTable.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Se o recurso foi encontrado (isPresent() == true), ele é atualizado.
        TableEntity tableEntity = optionalTable.get();

        // Atualiza apenas o nome com o valor recebido no RequestBody
        tableEntity.setName(table.getName());

        // Salva a entidade atualizada no banco de dados
        TableEntity updatedTable = repository.save(tableEntity);

        // Retorna a entidade atualizada com status 200 OK
        return ResponseEntity.ok(updatedTable);
    }
    public void delete(Long id) {
        logger.info("Deleting one TableEntity");

        var entity = repository.findById(id).orElseThrow(( ) -> new ResourceNotFoundException("TableEntity not found for this id!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(TableEntityDto dto) {
        dto.add(linkTo(methodOn(TableEntityController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(TableEntityController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(TableEntityController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(TableEntityController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(TableEntityController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
