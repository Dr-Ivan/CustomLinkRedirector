package com.example.redirector.slice;

import com.example.redirector.dto.CreateRedirectRequest;
import com.example.redirector.dto.CreateRedirectResponse;
import com.example.redirector.entity.LinkEntity;
import com.example.redirector.mapper.LinkMapper;
import com.example.redirector.repository.LinkRepository;
import com.example.redirector.service.LinkService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ServiceLayerCacheTest {

    @MockitoBean
    private LinkRepository mockLinkRepository;

    @MockitoBean
    private LinkMapper mockLinkMapper;

    @Autowired
    private LinkService linkService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("links_cache").clear();
    }

    @Test
    public void testDoubleGetFullLinkCall_shouldBeOneRepositoryCall() {
        // given
        String shortLink = "wiki";
        String fullLink = "https://wikipedia.org";
        LinkEntity entity = new LinkEntity(1L, shortLink, fullLink);

        when(mockLinkRepository.findByShortLink(shortLink)).thenReturn(Optional.of(entity));

        // when
        String resultCall1 = linkService.getFullLink(shortLink);
        String resultCall2 = linkService.getFullLink(shortLink);

        // then
        assertEquals(fullLink, resultCall1);
        assertEquals(fullLink, resultCall2);

        verify(mockLinkRepository, times(1)).findByShortLink(any());
    }

    @Test
    public void testTwoDifferentGetFullLinkCalls_shouldNotShareCache() {
        // given
        String shortLink1 = "wiki1";
        String fullLink1 = "https://wikipedia.org";
        LinkEntity entity1 = new LinkEntity(1L, shortLink1, fullLink1);

        String shortLink2 = "git";
        String fullLink2 = "https://github.com";
        LinkEntity entity2 = new LinkEntity(1L, shortLink2, fullLink2);

        when(mockLinkRepository.findByShortLink(shortLink1)).thenReturn(Optional.of(entity1));
        when(mockLinkRepository.findByShortLink(shortLink2)).thenReturn(Optional.of(entity2));

        // when
        String resultCall1 = linkService.getFullLink(shortLink1);
        String resultCall1Cached = linkService.getFullLink(shortLink1);
        String resultCall2 = linkService.getFullLink(shortLink2);
        String resultCall2Cached = linkService.getFullLink(shortLink2);

        // then
        assertEquals(fullLink1, resultCall1);
        assertEquals(fullLink2, resultCall2);
        assertEquals(resultCall1Cached, resultCall1);
        assertEquals(resultCall2Cached, resultCall2);
        assertNotEquals(resultCall1, resultCall2);

        verify(mockLinkRepository, times(1)).findByShortLink(shortLink1);
        verify(mockLinkRepository, times(1)).findByShortLink(shortLink2);
    }

    @Test
    public void testCacheDoesNotStoreNullValues() {
        // given
        String shortLink = "no-link";

        when(mockLinkRepository.findByShortLink(shortLink)).thenReturn(Optional.empty());

        // when
        assertThrows(
                EntityNotFoundException.class,
                () -> linkService.getFullLink(shortLink)
        );
        assertThrows(
                EntityNotFoundException.class,
                () -> linkService.getFullLink(shortLink)
        );

        // then
        verify(mockLinkRepository, times(2)).findByShortLink(any());
    }

    @Test
    public void testCacheEvictionOnPotentialUpdate() {
        // given
        String shortLink = "ggl";
        String fullLink = "https://google.com";
        LinkEntity entity = new LinkEntity(1L, shortLink, fullLink);

        CreateRedirectRequest request = new CreateRedirectRequest(shortLink, fullLink);
        CreateRedirectResponse response = new CreateRedirectResponse(1L, shortLink, fullLink);

        when(mockLinkRepository.findByShortLink(shortLink)).thenReturn(Optional.of(entity));
        when(mockLinkRepository.existsByShortLink(any())).thenReturn(false);

        when(mockLinkMapper.toResponse(entity)).thenReturn(response);
        when(mockLinkMapper.toEntity(request)).thenReturn(entity);

        // when
        linkService.getFullLink(shortLink);
        linkService.getFullLink(shortLink);

        linkService.addLink(request);

        linkService.getFullLink(shortLink);
        linkService.getFullLink(shortLink);


        // then
        verify(mockLinkRepository, times(2)).findByShortLink(any());
    }
}
