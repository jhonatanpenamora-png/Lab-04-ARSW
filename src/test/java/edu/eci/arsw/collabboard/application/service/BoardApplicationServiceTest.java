package edu.eci.arsw.collabboard.application.service;

import edu.eci.arsw.collabboard.application.exception.BoardNotFoundException;
import edu.eci.arsw.collabboard.domain.model.Board;
import edu.eci.arsw.collabboard.domain.model.BoardElement;
import edu.eci.arsw.collabboard.domain.model.ElementType;
import edu.eci.arsw.collabboard.infrastructure.persistence.InMemoryBoardRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardApplicationServiceTest {

    private final BoardApplicationService service =
            new BoardApplicationService(new InMemoryBoardRepository());

    @Test
    void shouldCreateAndReadBoard() {
        Board created = service.createBoard("Architecture Session");
        Board loaded = service.getBoard(created.id());

        assertEquals(created, loaded);
    }

    @Test
    void shouldFailWithConcreteExceptionWhenBoardDoesNotExist() {
        assertThrows(BoardNotFoundException.class,
                () -> service.getBoard("missing-board"));
    }

    @Test
    void shouldReplaceBoardKeepingSameId() {
        Board created = service.createBoard("Original");
        String originalId = created.id();

        List<BoardElement> newElements = List.of(
                new BoardElement("elem-1", ElementType.TEXT, 10, 20, 100, 50, "Updated content")
        );

        Board replaced = service.replaceBoard(originalId, "Updated Name", newElements);

        assertEquals(originalId, replaced.id());
        assertEquals("Updated Name", replaced.name());
        assertEquals(1, replaced.elements().size());
        assertEquals("elem-1", replaced.elements().get(0).id());
    }

    @Test
    void shouldFailWhenReplacingNonExistentBoard() {
        List<BoardElement> elements = List.of(
                new BoardElement("elem-1", ElementType.RECTANGLE, 0, 0, 10, 10, "")
        );

        assertThrows(BoardNotFoundException.class,
                () -> service.replaceBoard("non-existent", "Name", elements));
    }

    @Test
    void shouldFailWhenCreatingBoardWithBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createBoard("   "));
    }

    @Test
    void shouldReturnBoardWithEmptyElementsWhenCreated() {
        Board created = service.createBoard("Test Board");

        assertNotNull(created.id());
        assertEquals("Test Board", created.name());
        assertEquals(0, created.elements().size());
    }
}