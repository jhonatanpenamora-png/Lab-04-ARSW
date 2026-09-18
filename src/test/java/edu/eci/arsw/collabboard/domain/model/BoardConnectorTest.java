package edu.eci.arsw.collabboard.domain.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BoardConnectorTest {
    @Test
    void validConnectorReferencesExistingElements() {
        var a = new BoardElement("a", ElementType.RECTANGLE, 0, 0, 100, 60, "A", null, null);
        var b = new BoardElement("b", ElementType.TEXT, 200, 0, 100, 30, "B", null, null);
        var c = new BoardElement("c", ElementType.CONNECTOR, 0, 0, 0, 0, "", "a", "b");
        assertDoesNotThrow(() -> new Board("board", "Demo", List.of(a, b, c)));
    }

    @Test
    void connectorCannotReferenceMissingElement() {
        var a = new BoardElement("a", ElementType.RECTANGLE, 0, 0, 100, 60, "A", null, null);
        var c = new BoardElement("c", ElementType.CONNECTOR, 0, 0, 0, 0, "", "a", "missing");
        assertThrows(IllegalArgumentException.class, () -> new Board("board", "Demo", List.of(a, c)));
    }

    @Test
    void connectorEndpointsMustBeDifferent() {
        var a = new BoardElement("a", ElementType.RECTANGLE, 0, 0, 100, 60, "A", null, null);
        assertThrows(IllegalArgumentException.class, () -> new BoardElement("c", ElementType.CONNECTOR, 0, 0, 0, 0, "", "a", "a"));
    }

    @Test
    void connectorRequiresSourceAndTarget() {
        var a = new BoardElement("a", ElementType.RECTANGLE, 0, 0, 100, 60, "A", null, null);
        assertThrows(IllegalArgumentException.class, () -> new BoardElement("c", ElementType.CONNECTOR, 0, 0, 0, 0, "", "", "b"));
    }

    @Test
    void nonConnectorIgnoresSourceAndTarget() {
        var e = new BoardElement("e", ElementType.RECTANGLE, 10, 10, 100, 50, "API", "ignored", "ignored");
        assertNull(e.sourceId());
        assertNull(e.targetId());
    }

    @Test
    void duplicateElementIdsRejected() {
        var a = new BoardElement("a", ElementType.RECTANGLE, 0, 0, 100, 60, "A", null, null);
        var b = new BoardElement("a", ElementType.TEXT, 200, 0, 100, 30, "B", null, null);
        assertThrows(IllegalArgumentException.class, () -> new Board("board", "Demo", List.of(a, b)));
    }

    @Test
    void connectorCannotReferenceAnotherConnector() {
        var a = new BoardElement("a", ElementType.RECTANGLE, 0, 0, 100, 60, "A", null, null);
        var b = new BoardElement("b", ElementType.TEXT, 200, 0, 100, 30, "B", null, null);
        var firstConnector = new BoardElement("c1", ElementType.CONNECTOR, 0, 0, 0, 0, "", "a", "b");
        var invalidConnector = new BoardElement("c2", ElementType.CONNECTOR, 0, 0, 0, 0, "", "c1", "b");

        assertThrows(IllegalArgumentException.class,
                () -> new Board("board", "Demo", List.of(a, b, firstConnector, invalidConnector)));
    }

    @Test
    void legacyConstructorKeepsLabFourCallersCompatible() {
        var element = new BoardElement("a", ElementType.RECTANGLE, 0, 0, 100, 60, "A");

        assertNull(element.sourceId());
        assertNull(element.targetId());
    }
}
