package com.pizzaria_system.controller;

import com.pizzaria_system.data.dto.TableEntityDto;
import com.pizzaria_system.model.TableEntity;
import com.pizzaria_system.services.TableEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/table")
@RestController
public class TableEntityController {
    @Autowired
    private TableEntityService service;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<TableEntityDto> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public TableEntityDto findById(@PathVariable("id") Long id) {
        return  service.findById(id);
    }

    @PostMapping()
    public TableEntityDto create(@RequestBody TableEntityDto TableEntity) {
        return service.create(TableEntity);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TableEntityDto update(@PathVariable("id") Long id, @RequestBody TableEntityDto TableEntity) {
        TableEntity.setId(id);
        return service.update(TableEntity);
    }

    @PutMapping(value = "/update-table/{id}")
    public ResponseEntity<TableEntity> editTableName(@PathVariable Long id,@RequestBody TableEntity table ) {
        return service.tableEditName(id, table);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
