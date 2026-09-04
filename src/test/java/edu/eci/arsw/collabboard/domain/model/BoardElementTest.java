package edu.eci.arsw.collabboard.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardElementTest {

    @Test
    void shouldRejectMissingIdentity() {
        assertThrows(IllegalArgumentException.class,
                () -> new BoardElement(null, ElementType.TEXT, 0, 0, 10, 10, "text"));
        assertThrows(IllegalArgumentException.class,
                () -> new BoardElement(" ", ElementType.TEXT, 0, 0, 10, 10, "text"));
    }

    @Test
    void shouldRejectMissingType() {
        assertThrows(IllegalArgumentException.class,
                () -> new BoardElement("element-1", null, 0, 0, 10, 10, "text"));
    }

    @Test
    void shouldRejectNegativeDimensions() {
        assertThrows(IllegalArgumentException.class,
                () -> new BoardElement("element-1", ElementType.RECTANGLE,
                        0, 0, -1, 10, ""));
        assertThrows(IllegalArgumentException.class,
                () -> new BoardElement("element-1", ElementType.RECTANGLE,
                        0, 0, 10, -1, ""));
    }

    @Test
    void shouldNormalizeNullTextToEmptyString() {
        BoardElement element = new BoardElement(
                "element-1", ElementType.TEXT, 0, 0, 10, 10, null
        );

        assertEquals("", element.text());
    }
}
