package com.design_pattern.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryDto {
    private String name;
    private String description;
    private List<ComponentDto> component;
}
