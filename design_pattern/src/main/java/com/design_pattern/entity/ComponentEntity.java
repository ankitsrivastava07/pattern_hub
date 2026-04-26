package com.design_pattern.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponentEntity {
    private String id;
    private String name;
    private String description;

    public ComponentEntity(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
