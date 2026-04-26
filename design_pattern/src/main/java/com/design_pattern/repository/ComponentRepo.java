package com.design_pattern.repository;

import com.design_pattern.entity.ComponentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComponentRepo extends MongoRepository<ComponentEntity, String> {
}
