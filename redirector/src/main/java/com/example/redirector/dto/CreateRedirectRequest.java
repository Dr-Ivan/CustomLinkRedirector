package com.example.redirector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class CreateRedirectRequest {

    @NotEmpty
    @NotBlank
    @NotNull
    @JsonProperty("shortName")
    private String shortName;

    @URL
    @JsonProperty("realLink")
    private String realLink;
}
