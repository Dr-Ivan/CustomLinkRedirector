package com.example.redirector.controller;

import com.example.redirector.dto.CreateRedirectRequest;
import com.example.redirector.dto.CreateRedirectResponse;
import com.example.redirector.service.LinkService;
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
public class LinkController {

    private final LinkService linkService;

    @GetMapping("/{shortLink}")
    public ResponseEntity<Void> redirect(
            @PathVariable("shortLink") String shortLink
    ) {
        String link = linkService.getFullLink(shortLink);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, link)
                .build();
    }

    @PostMapping
    public ResponseEntity<CreateRedirectResponse> createRedirect(
            @Valid @RequestBody CreateRedirectRequest createRedirectRequest
    ) {
        CreateRedirectResponse response = linkService.addLink(createRedirectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
