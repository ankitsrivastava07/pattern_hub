package com.design_pattern.service;

import com.design_pattern.dto.ApiResponse;
import com.design_pattern.dto.ComponentDto;
import com.design_pattern.dto.RequestDto;

import java.util.Map;

public interface ApiService {
    ApiResponse createResource(RequestDto requestDto);

    ApiResponse findByCategoryName(String categoryName);

    ApiResponse getAllResource();

    ApiResponse deleteComponentById(String categoryId, String componentId);

    ApiResponse deleteResourceById(String categoryId);

    ApiResponse addComponent(String categoryId, ComponentDto componentDto);

    ApiResponse updateComponent(String categoryId, String componentId, ComponentDto componentDto);
}
