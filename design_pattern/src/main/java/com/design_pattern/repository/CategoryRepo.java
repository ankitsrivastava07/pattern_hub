package com.design_pattern.repository;

import com.design_pattern.entity.CategoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepo extends MongoRepository<CategoryEntity, String> {

    CategoryEntity findByName(String name);
}
