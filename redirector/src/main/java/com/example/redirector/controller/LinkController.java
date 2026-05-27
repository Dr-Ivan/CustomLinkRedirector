package com.example.redirector.controller;

import com.example.redirector.dto.CreateRedirectRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/link")
@CrossOrigin("*")
public class LinkController {

    private String realLink = "https://google.com";

    @GetMapping
    public ResponseEntity<Void> redirect() {
        String link = this.realLink;

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, link)
                .build();
    }

    @PostMapping
    public ResponseEntity<Void> createRedirect(@RequestBody @Valid CreateRedirectRequest createRedirectRequest){
        this.realLink = createRedirectRequest.getRealLink();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
