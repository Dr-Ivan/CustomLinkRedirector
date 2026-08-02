package com.example.redirector.unit;

import com.example.redirector.dto.CreateRedirectRequest;
import com.example.redirector.entity.LinkEntity;
import com.example.redirector.mapper.LinkMapper;
import com.example.redirector.repository.LinkRepository;
import com.example.redirector.service.LinkService;
import com.example.redirector.dto.CreateRedirectResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    @Mock
    private LinkMapper mockLinkMapper;

    @Mock
    private LinkRepository mockLinkRepository;

    @InjectMocks
    private LinkService linkService;

    @Test
    public void getFullLink_whenShortLinkExists_shouldReturnFullLink() {
        // given
        String shortLink = "wiki";
        String fullLink = "https://wikipedia.org";

        when(mockLinkRepository.findByShortLink(shortLink))
                .thenReturn(Optional.of(
                        new LinkEntity(1L, shortLink, fullLink)
                ));

        // when
        String result = linkService.getFullLink(shortLink);

        // then
        assertEquals(fullLink, result);
    }

    @Test
    public void getFullLink_whenShortLinkNotExists_shouldThrowException() {
        // given
        String shortLink = "no-link";
        when(mockLinkRepository.findByShortLink(shortLink))
                .thenReturn(Optional.empty());

        // when and then
        assertThrows(
                EntityNotFoundException.class,
                () -> linkService.getFullLink(shortLink)
        );
    }


    @Test
    public void addLink_whenNameIsFree_shouldReturnCreateRedirectResponse(){
        // given
        String shortLink = "wiki";
        String fullLink = "https://wikipedia.org";
        LinkEntity entity = new LinkEntity(1L, shortLink, fullLink);
        CreateRedirectRequest request = new CreateRedirectRequest(shortLink, fullLink);
        CreateRedirectResponse expected = new CreateRedirectResponse(1L, shortLink, fullLink);

        when(mockLinkRepository.existsByShortLink(shortLink))
                .thenReturn(false);
        when(mockLinkRepository.save(entity)).thenReturn(entity);
        when(mockLinkMapper.toEntity(request)).thenReturn(entity);
        when(mockLinkMapper.toResponse(entity)).thenReturn(expected);

        // when
        CreateRedirectResponse result = linkService.addLink(request);

        // then
        assertEquals(expected, result);

        verify(mockLinkRepository, times(1)).existsByShortLink(any());
        verify(mockLinkRepository, times(1)).save(any());
        verify(mockLinkMapper, times(1)).toEntity(any());
        verify(mockLinkMapper, times(1)).toResponse(any());

    }

    @Test
    public void addLink_whenNameIsTaken_shouldThrowException(){
        // given
        String shortLink = "wiki";
        String fullLink = "https://wikipedia.org";
        CreateRedirectRequest request = new CreateRedirectRequest(shortLink, fullLink);

        when(mockLinkRepository.existsByShortLink(shortLink))
                .thenReturn(true);

        // when and then
        assertThrows(
                IllegalArgumentException.class,
                () -> linkService.addLink(request)
        );

        verify(mockLinkRepository, never()).save(any());
        verify(mockLinkRepository, times(1)).existsByShortLink(any());
    }
}
