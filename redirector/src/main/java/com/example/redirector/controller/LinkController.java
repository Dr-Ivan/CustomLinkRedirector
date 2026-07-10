package com.example.redirector.controller;

import com.example.redirector.dto.CreateRedirectRequest;
import com.example.redirector.dto.CreateRedirectResponse;
import com.example.redirector.dto.ErrorResponse;
import com.example.redirector.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/link")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Redirect Controller", description = "Управление короткими ссылками")
public class LinkController {

    private final LinkService linkService;

    @GetMapping("/{shortLink}")
    @Operation(
            summary = "Перенаправление по короткой ссылке",
            description = "Принимает короткую ссылку и возвращает HTTP 302 с Location на полный URL"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Успешное перенаправление — клиент будет переадресован"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Короткая ссылка не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> redirect(
            @Parameter(
                    description = "Короткая ссылка",
                    required = true,
                    example = "my_link"
            )
            @PathVariable("shortLink") String shortLink
    ) {
        String link = linkService.getFullLink(shortLink);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, link)
                .build();
    }

    @PostMapping
    @Operation(
            summary = "Создание новой короткой ссылки",
            description = "Создает новую короткую ссылку. Короткая ссылка должна быть уникальной"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ссылка успешно создана",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateRedirectResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные входные данные (пустая короткая ссылка или некорректный URL)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт: короткая ссылка уже существует",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CreateRedirectResponse> createRedirect(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания короткой ссылки",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "shortLink": "git",
                                              "fullLink": "https://github.com"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody CreateRedirectRequest createRedirectRequest
    ) {
        CreateRedirectResponse response = linkService.addLink(createRedirectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
