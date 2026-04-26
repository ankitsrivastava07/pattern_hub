package com.design_pattern.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ApiResponse {
    private String msg;
    private Boolean status;
    private Object data;
    private Object error;

    public ApiResponse(String message, Boolean status, Object data, Object error) {
        this.msg = message;
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static ApiResponseBuilder builder() {
        return new ApiResponseBuilder();
    }

    public static class ApiResponseBuilder {
        private String message = "";
        private Boolean status = false;
        private Object data = new ArrayList<>();
        private Object error = new ArrayList<>();

        public ApiResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder status(Boolean status) {
            this.status = status;
            return this;
        }

        public ApiResponseBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder error(Object error) {
            this.error = error;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(this.message, this.status, this.data, this.error);
        }

    }
}
