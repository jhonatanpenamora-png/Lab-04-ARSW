package edu.eci.arsw.collabboard.domain.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Board(String id, String name, List<BoardElement> elements) {
    public Board {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Board id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Board name is required");
        }
        elements = elements == null ? List.of() : List.copyOf(elements);
        validateConnectors(elements);
    }

    private static void validateConnectors(List<BoardElement> elements) {
        Set<String> ids = new HashSet<>();
        for (BoardElement e : elements) {
            if (!ids.add(e.id())) {
                throw new IllegalArgumentException("Duplicate element id: " + e.id());
            }
        }

        Map<String, BoardElement> elementsById = elements.stream()
                .collect(Collectors.toMap(BoardElement::id, Function.identity()));

        for (BoardElement e : elements) {
            if (e.type() != ElementType.CONNECTOR) {
                continue;
            }

            BoardElement source = elementsById.get(e.sourceId());
            BoardElement target = elementsById.get(e.targetId());
            if (source == null || target == null) {
                throw new IllegalArgumentException(
                        "Connector endpoints must reference existing elements"
                );
            }
            if (source.type() == ElementType.CONNECTOR || target.type() == ElementType.CONNECTOR) {
                throw new IllegalArgumentException(
                        "Connector endpoints must reference non-connector elements"
                );
            }
        }
    }
}
