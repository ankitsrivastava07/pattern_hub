package com.design_pattern.dao;

import com.design_pattern.entity.CategoryEntity;
import com.design_pattern.entity.ComponentEntity;

import java.util.List;

public interface ApiDaoService {
    CategoryEntity findByCategoryName(String categoryName);

    List<CategoryEntity> findAll();

    CategoryEntity createResource(CategoryEntity categoryEntity);

    List<ComponentEntity> deleteComponentById(String categoryId, String componentId);

    CategoryEntity deleteResourceById(String resourceId);

    ComponentEntity addComponent(String categoryId, ComponentEntity newComponent);
}
