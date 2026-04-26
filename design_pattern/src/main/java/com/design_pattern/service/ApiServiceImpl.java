package com.design_pattern.service;

import com.design_pattern.dao.ApiDaoService;
import com.design_pattern.dto.ApiResponse;
import com.design_pattern.dto.ComponentDto;
import com.design_pattern.dto.RequestDto;
import com.design_pattern.entity.CategoryEntity;
import com.design_pattern.entity.ComponentEntity;
import com.design_pattern.exceptionHandler.exception.ResourceAlreadyExistException;
import com.design_pattern.exceptionHandler.exception.ResourceNotFoundException;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApiServiceImpl implements ApiService {
    @Autowired
    private ApiDaoService apiDaoService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public ApiResponse createResource(RequestDto requestDto) {
        log.info("Processing request to create resource {}", requestDto);
        if (apiDaoService.findByCategoryName(requestDto.getCategory().getName()) != null) {
            throw new ResourceAlreadyExistException("Category already exist " + requestDto.getCategory().getName());
        }
        try {
            CategoryEntity categoryEntity = new CategoryEntity(
                    requestDto.getCategory().getName(),
                    requestDto.getCategory().getDescription(),
                    requestDto.getCategory().getComponent().stream()
                            .map(e -> new ComponentEntity(UUID
                                    .randomUUID().toString(),
                                    e.getName(), e.getDescription()))
                            .collect(Collectors.toList()));

            categoryEntity = apiDaoService.createResource(categoryEntity);
            ApiResponse response = ApiResponse
                    .builder()
                    .data(categoryEntity)
                    .status(Boolean.TRUE)
                    .message("Resource created successfully")
                    .build();

            log.info("Resource creation completed successfully");
            return response;
        } catch (Exception e) {
            log.error("Exception occurred while creating resource", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse findByCategoryName(String categoryName) {
        return ApiResponse
                .builder()
                .message("Resource found successfully")
                .data(apiDaoService.findByCategoryName(categoryName))
                .build();
    }

    @Override
    public ApiResponse getAllResource() {
        return ApiResponse
                .builder()
                .data(apiDaoService.findAll())
                .message("Fetched all resources successfully")
                .build();
    }

    @Override
    public ApiResponse deleteComponentById(String categoryId, String componentId) {
        return ApiResponse.builder()
                .status(Boolean.TRUE)
                .data(apiDaoService.deleteComponentById(categoryId, componentId))
                .message("Component deleted successfully").build();

    }

    @Override
    public ApiResponse deleteResourceById(String resourceId) {
        return ApiResponse.builder()
                .message("Resource deleted successfully")
                .status(Boolean.TRUE)
                .data(apiDaoService.deleteResourceById(resourceId))
                .build();
    }

    @Override
    public ApiResponse addComponent(String categoryId, ComponentDto componentDto) {
        ComponentEntity component = new ComponentEntity(UUID
                .randomUUID()
                .toString(),
                componentDto.getName(),
                componentDto.getDescription());
        component = apiDaoService.addComponent(categoryId, component);
        return ApiResponse
                .builder()
                .data(component)
                .message("Component added successfully")
                .status(Boolean.TRUE)
                .build();
    }

    @Override
    public ApiResponse updateComponent(String categoryId, String componentId, ComponentDto componentDto) {
        ComponentEntity component = new ComponentEntity(componentId,
                componentDto.getName(),
                componentDto.getDescription());

        Query updateQuery = new Query(Criteria.where("_id").is(categoryId).and("component._id").is(componentId));

        Update update = new Update()
                .set("component.$", component);

        UpdateResult writeResult = mongoTemplate.updateFirst(updateQuery, update, "category");

        if (writeResult.getMatchedCount() == 0) {
            throw new ResourceNotFoundException("Component not found with ID: " + componentId);
        }

        // 3. High-Performance Aggregation Fetch (Your Logic)
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(categoryId)),
                Aggregation.project().and("component").filter(
                        "item",
                        ComparisonOperators.Eq.valueOf("item._id").equalToValue(componentId)
                ).as("matchedComponent"),
                Aggregation.unwind("matchedComponent"),
                Aggregation.replaceRoot("matchedComponent")
        );

        component = mongoTemplate.aggregate(aggregation, "category", ComponentEntity.class)
                .getUniqueMappedResult();

        return ApiResponse.builder()
                .message("Component updated successfully")
                .data(component).
                status(Boolean.TRUE)
                .build();
    }

}
