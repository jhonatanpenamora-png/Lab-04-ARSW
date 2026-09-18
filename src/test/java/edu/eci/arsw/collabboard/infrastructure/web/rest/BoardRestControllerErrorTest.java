package edu.eci.arsw.collabboard.infrastructure.web.rest;

import edu.eci.arsw.collabboard.application.exception.BoardNotFoundException;
import edu.eci.arsw.collabboard.application.service.BoardApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardRestController.class)
class BoardRestControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardApplicationService service;

    @Test
    void getUnknownBoardReturnsUniform404() throws Exception {
        when(service.getBoard("missing")).thenThrow(new BoardNotFoundException("missing"));

        mockMvc.perform(get("/api/boards/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("BOARD_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/boards/missing"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createWithBlankNameReturns400WithFieldMessage() throws Exception {
        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("name: name is required"));
    }

    @Test
    void createWithMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ this is not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void replaceOfUnknownBoardReturnsUniform404() throws Exception {
        when(service.replaceBoard(anyString(), anyString(), anyList()))
                .thenThrow(new BoardNotFoundException("missing"));

        mockMvc.perform(put("/api/boards/missing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"elements\":[]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BOARD_NOT_FOUND"));
    }

    @Test
    void replaceWithInvalidConnectorReturnsUniform400() throws Exception {
        when(service.replaceBoard(anyString(), anyString(), anyList()))
                .thenThrow(new IllegalArgumentException(
                        "Connector endpoints must reference existing elements"
                ));

        mockMvc.perform(put("/api/boards/board-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Updated",
                                  "elements":[
                                    {"id":"connector","type":"CONNECTOR","x":0,"y":0,"width":0,"height":0,"text":"","sourceId":"missing-a","targetId":"missing-b"}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value(
                        "Connector endpoints must reference existing elements"
                ));
    }
}
