package com.design_pattern.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "category")
public class CategoryEntity {
    @Id
    @JsonProperty("_id")
    private String _id;
    private String name;
    private String description;
    private List<ComponentEntity> component;

    public CategoryEntity(String name, String description, List<ComponentEntity> component) {
        this.name = name;
        this.description = description;
        this.component = component;

    }
}
