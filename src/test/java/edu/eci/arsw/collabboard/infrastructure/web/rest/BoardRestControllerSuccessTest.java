package edu.eci.arsw.collabboard.infrastructure.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arsw.collabboard.application.service.BoardApplicationService;
import edu.eci.arsw.collabboard.domain.model.Board;
import edu.eci.arsw.collabboard.domain.model.BoardElement;
import edu.eci.arsw.collabboard.domain.model.ElementType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardRestController.class)
class BoardRestControllerSuccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardApplicationService service;

    @Test
    void createReturnsCreatedWithBoardBody() throws Exception {
        Board created = new Board("board-1", "Architecture Board", List.of());
        when(service.createBoard(anyString())).thenReturn(created);

        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateBoardRequest("Architecture Board"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("board-1"))
                .andExpect(jsonPath("$.name").value("Architecture Board"));
    }

    @Test
    void getReturnsOkWithBoardBody() throws Exception {
        Board board = new Board("board-1", "Architecture Board", List.of());
        when(service.getBoard("board-1")).thenReturn(board);

        mockMvc.perform(get("/api/boards/board-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("board-1"))
                .andExpect(jsonPath("$.name").value("Architecture Board"));
    }

    @Test
    void replaceReturnsOkAndKeepsPathId() throws Exception {
        BoardElement element = new BoardElement("element-1", ElementType.TEXT, 20, 30, 160, 40, "Application Service");
        Board replaced = new Board("board-1", "Updated Board", List.of(element));
        when(service.replaceBoard(anyString(), anyString(), anyList())).thenReturn(replaced);

        ReplaceBoardRequest request = new ReplaceBoardRequest("Updated Board", List.of(element));

        mockMvc.perform(put("/api/boards/board-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("board-1"))
                .andExpect(jsonPath("$.name").value("Updated Board"))
                .andExpect(jsonPath("$.elements[0].id").value("element-1"));
    }
}
