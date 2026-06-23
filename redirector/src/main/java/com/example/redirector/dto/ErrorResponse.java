package com.example.redirector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse {

    @JsonProperty("exception")
    private String exception;

    @JsonProperty("details")
    private String details;

    @JsonProperty("timestamp")
    private Instant timestamp;
}
