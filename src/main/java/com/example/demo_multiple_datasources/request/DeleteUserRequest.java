package com.example.demo_multiple_datasources.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeleteUserRequest {
    
    @JsonProperty("id")
    private String id;
}
