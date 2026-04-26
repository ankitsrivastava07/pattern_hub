package com.design_pattern.dao;

import com.design_pattern.entity.CategoryEntity;
import com.design_pattern.entity.ComponentEntity;
import com.design_pattern.exceptionHandler.exception.ResourceNotFoundException;
import com.design_pattern.repository.CategoryRepo;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class ApiDaoImpl implements ApiDaoService {
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CategoryEntity createResource(CategoryEntity categoryEntity) {
        return categoryRepo.save(categoryEntity);
    }

    @Override
    public CategoryEntity findByCategoryName(String categoryName) {
        return categoryRepo.findByName(categoryName);
    }

    @Override
    public List<CategoryEntity> findAll() {
        return categoryRepo.findAll();
    }

    @Override
    public List<ComponentEntity> deleteComponentById(String categoryId, String componentId) {
        Object pId = org.bson.types.ObjectId.isValid(categoryId) ? new org.bson.types.ObjectId(categoryId) : categoryId;
        Object cId = org.bson.types.ObjectId.isValid(componentId) ? new org.bson.types.ObjectId(componentId) : componentId;

        Aggregation aggregation = Aggregation.newAggregation(
                // 1. Fast indexed lookup of the parent
                Aggregation.match(Criteria.where("_id").is(pId)),

                // 2. Project ONLY the specific child using internal filtering
                Aggregation.project().and("component").filter("item", ComparisonOperators.Eq.valueOf("item._id").equalToValue(cId)).as("matchedComponent"),

                // 3. Unwind only the SINGLE result (much faster than unwinding 500)
                Aggregation.unwind("matchedComponent"),

                // 4. Return as the entity
                Aggregation.replaceRoot("matchedComponent"));

        ComponentEntity result = mongoTemplate.aggregate(aggregation,
                        "category", ComponentEntity.class)
                .getUniqueMappedResult();

        // 2. Explicit existence check
        if (result == null) {
            throw new ResourceNotFoundException(String.format("Component with ID [%s] not found in Category [%s]", componentId, categoryId));
        }
        Query query = new Query(Criteria.where("_id").is(categoryId));

        Update update = new Update().pull("component", new Query(Criteria.where("id").is(componentId)).getQueryObject());

        mongoTemplate.updateFirst(query, update, CategoryEntity.class);
        return Arrays.asList(result);
    }

    public CategoryEntity deleteResourceById(String resourceId) {
        // 1. Convert to ObjectId safely to avoid the "hexString" error
        Object pId = org.bson.types.ObjectId.isValid(resourceId) ? new org.bson.types.ObjectId(resourceId) : resourceId;
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(pId));

        CategoryEntity entity = mongoTemplate.findAndRemove(query, CategoryEntity.class);
        if (entity == null) {
            throw new ResourceNotFoundException(String.format("Resource with ID [%s] not found", resourceId));
        }

        return entity;
    }

    @Override
    public ComponentEntity addComponent(String categoryId, ComponentEntity newComponent) {
        // 1. Ensure the new component has a valid ID if not already set
        if (newComponent.getId() == null || newComponent.getId().isEmpty()) {
            newComponent.setId(new org.bson.types.ObjectId().toHexString());
        }

        // 2. Safely handle the Category ID conversion
        Object pId = org.bson.types.ObjectId.isValid(categoryId)
                ? new org.bson.types.ObjectId(categoryId) : categoryId;

        // 3. Build the Update query
        Query query = new Query(Criteria.where("_id").is(pId));

        // Using $push to add the item to the 'component' array
        Update update = new Update().push("component", newComponent);

        // 4. Execute updateFirst
        UpdateResult result = mongoTemplate.updateFirst(query, update, CategoryEntity.class);

        // 5. Validation: If no document was matched, the category doesn't exist
        if (result.getMatchedCount() == 0) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }

        return newComponent;
    }
}
