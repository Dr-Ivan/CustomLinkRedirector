package com.example.redirector.slice;

import com.example.redirector.controller.LinkController;
import com.example.redirector.dto.CreateRedirectResponse;
import com.example.redirector.service.LinkService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LinkController.class)
public class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkService mockLinkService;

    @MockitoBean
    private CacheManager cacheManager;


    @Test
    public void testRedirect_whenLinkExists_shouldReturnRedirectWithLocation() throws Exception {
        // given
        String shortLink = "wiki";
        String fullLink = "https://wikipedia.org";

        when(mockLinkService.getFullLink(shortLink)).thenReturn(fullLink);

        // when and then
        mockMvc.perform(get("/link/{shortName}", shortLink))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists("location"))
                .andExpect(header().string("location", fullLink));

    }

    @Test
    public void testLinkCreation_whenNameIsFree_shouldReturnCreatedStatus() throws Exception {
        // given
        String shortLink = "wiki";
        String fullLink = "https://wikipedia.org";

        String json = """
                {
                    "shortLink": "%s",
                    "fullLink": "%s"
                }
                """.formatted(shortLink, fullLink);

        CreateRedirectResponse serviceResponse = new CreateRedirectResponse(1L, shortLink, fullLink);
        when(mockLinkService.addLink(any())).thenReturn(serviceResponse);

        // when and then
        mockMvc.perform(
                post("/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shortLink").value(shortLink))
                .andExpect(jsonPath("$.fullLink").value(fullLink));

    }

    @Test
    public void testLinkCreation_whenNameIsBlank_shouldReturnClientErrorStatus() throws Exception {
        // given
        String fullLink = "https://wikipedia.org";

        String json = """
                {
                    "shortLink": " ",
                    "fullLink": "%s"
                }
                """.formatted(fullLink);
        when(mockLinkService.addLink(any())).thenThrow(IllegalArgumentException.class);

        // when and then
        mockMvc.perform(
                        post("/link")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andExpect(status().is4xxClientError());

    }

}
