package com.example.redirector.slice;

import com.example.redirector.entity.LinkEntity;
import com.example.redirector.repository.LinkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LinkRepositoryTest {

    @Autowired
    private LinkRepository linkRepository;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    public void testFindByShortLink_whenShortLinkExists_shouldReturnPresentOptional() {
        // given
        String shortLink = "wiki";
        String fullLink = "https://wikipedia.org";

        LinkEntity entity = new LinkEntity(null, shortLink, fullLink);

        if (!linkRepository.existsByShortLink(shortLink)) linkRepository.save(entity);


        // when
        Optional<LinkEntity> result = linkRepository.findByShortLink(shortLink);

        // then
        assertTrue(result.isPresent());
        // assertEquals(1L, result.get().getId());
        assertEquals(fullLink, result.get().getFullLink());
        assertEquals(shortLink, result.get().getShortLink());
    }

    @Test
    public void testFindByShortLink_whenShortLinkNotExist_shouldReturnEmptyOptional() {
        // given
        String shortLink = "no-link";

        // when
        Optional<LinkEntity> result = linkRepository.findByShortLink(shortLink);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    public void testNonExitingLink_shouldReturnFalse() {
        String shortLink = "no-link";

        boolean result = linkRepository.existsByShortLink(shortLink);

        assertFalse(result);
    }

}
