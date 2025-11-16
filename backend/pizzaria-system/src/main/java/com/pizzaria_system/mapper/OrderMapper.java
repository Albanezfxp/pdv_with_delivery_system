package com.pizzaria_system.mapper;

import com.pizzaria_system.data.dto.OrderEntityDto;
import com.pizzaria_system.model.OrderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderEntityDto toDto(OrderEntity entity) {
        if (entity == null) return null;

        OrderEntityDto dto = new OrderEntityDto();
        dto.setId(entity.getId());
        dto.setTotal(entity.getTotal());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setStatus(entity.getStatus());
        dto.setAddition(entity.getAddition());
        dto.setClient(entity.getClient());
        return dto;
    }

    public static List<OrderEntityDto> toDtoList(List<OrderEntity> entities) {
        if (entities == null) return new ArrayList<>();

        return entities.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }
}