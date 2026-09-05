package edu.eci.arsw.collabboard.domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardTest {

    @Test
    void shouldRejectMissingIdentity() {
        assertThrows(IllegalArgumentException.class,
                () -> new Board(null, "Architecture", List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new Board(" ", "Architecture", List.of()));
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Board("board-1", null, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new Board("board-1", " ", List.of()));
    }

    @Test
    void shouldNormalizeNullElementsToEmptyList() {
        Board board = new Board("board-1", "Architecture", null);

        assertEquals(List.of(), board.elements());
    }

    @Test
    void shouldDefensivelyCopyElements() {
        List<BoardElement> mutableElements = new ArrayList<>();
        Board board = new Board("board-1", "Architecture", mutableElements);

        mutableElements.add(new BoardElement(
                "element-1", ElementType.TEXT, 0, 0, 120, 30, "Hello"
        ));

        assertEquals(List.of(), board.elements());
        assertThrows(UnsupportedOperationException.class,
                () -> board.elements().add(new BoardElement(
                        "element-2", ElementType.RECTANGLE, 0, 0, 20, 20, ""
                )));
    }
}