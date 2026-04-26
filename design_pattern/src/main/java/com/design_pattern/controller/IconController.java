package com.design_pattern.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IconController {

    @RequestMapping("/favicon.ico")
    public ResponseEntity<?> getIcon() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
