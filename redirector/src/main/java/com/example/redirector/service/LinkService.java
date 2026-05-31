package com.example.redirector.service;

import com.example.redirector.dto.CreateRedirectRequest;
import com.example.redirector.dto.CreateRedirectResponse;
import com.example.redirector.entity.LinkEntity;
import com.example.redirector.mapper.LinkMapper;
import com.example.redirector.repository.LinkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LinkService {
    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;

    public String getFullLink(String shortLink) {

        return linkRepository.findByShortLink(shortLink)
                .orElseThrow(() -> new EntityNotFoundException("This short link does not exist"))
                .getFullLink();

    }

    private CreateRedirectResponse addLink(CreateRedirectRequest request) {

        if (linkRepository.existsByShortLink(request.getShortName())) {
            throw new IllegalArgumentException("This short name is already taken");
        }

        LinkEntity toSave = linkMapper.toEntity(request);
        LinkEntity saved = linkRepository.save(toSave);

        return linkMapper.toResponse(saved);

    }
}
