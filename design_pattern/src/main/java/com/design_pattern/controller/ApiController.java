package com.design_pattern.controller;

import com.design_pattern.dto.ApiResponse;
import com.design_pattern.dto.ComponentDto;
import com.design_pattern.dto.RequestDto;
import com.design_pattern.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/category")
public class ApiController {

    @Autowired
    private ApiService apiService;
    private Logger logger = LoggerFactory.getLogger("");

    @PostMapping
    public ResponseEntity<?> createResource(@RequestBody RequestDto requestDto) {
        log.info("Creating resource with request: {}", requestDto);
        var result = apiService.createResource(requestDto);
        log.debug("Resource created successfully: {}", result);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllResources() {
        logger.info("Fetch all resources method start ");
        ApiResponse apiResponse = apiService.getAllResource();
        logger.info("Fetch all resource method end here ");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}/component/{componentId}")
    public ResponseEntity<?> deleteResourceById(@PathVariable String categoryId,
                                                @PathVariable String componentId) {
        return new ResponseEntity<>(apiService
                .deleteComponentById(categoryId, componentId), HttpStatus.OK);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<?> deleteResourcesById(@PathVariable String resourceId) {
        return new ResponseEntity<>(apiService.deleteResourceById(resourceId), HttpStatus.OK);
    }

    @GetMapping("/{categoryName}")
    public ResponseEntity<?> findByCategoryName(@PathVariable String categoryName) {
        return new ResponseEntity<>(apiService.findByCategoryName(categoryName), HttpStatus.OK);
    }

    @PostMapping("/{categoryId}/component")
    public ResponseEntity<?> addComponent(@PathVariable String categoryId,
                                          @RequestBody ComponentDto componentDto) {
        return new ResponseEntity<>(apiService
                .addComponent(categoryId, componentDto), HttpStatus.OK);
    }

    @PutMapping("/{categoryId}/component/{componentId}")
    public ResponseEntity<?> updateComponent(
            @PathVariable String categoryId,
            @PathVariable String componentId,
            @RequestBody ComponentDto componentDto) {

        ApiResponse apiResponse = apiService.updateComponent(categoryId, componentId, componentDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
