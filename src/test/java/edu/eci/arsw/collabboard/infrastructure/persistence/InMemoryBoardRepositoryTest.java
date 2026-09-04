package edu.eci.arsw.collabboard.infrastructure.persistence;

import edu.eci.arsw.collabboard.domain.model.Board;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryBoardRepositoryTest {

    private final InMemoryBoardRepository repository = new InMemoryBoardRepository();

    @Test
    void shouldSaveAndFindBoard() {
        Board board = new Board("board-1", "Architecture", List.of());

        Board saved = repository.save(board);

        assertEquals(board, saved);
        assertEquals(board, repository.findById("board-1").orElseThrow());
        assertTrue(repository.existsById("board-1"));
    }

    @Test
    void shouldReturnEmptyForMissingBoard() {
        assertTrue(repository.findById("missing").isEmpty());
        assertFalse(repository.existsById("missing"));
    }

    @Test
    void shouldReplaceBoardWithSameIdentity() {
        repository.save(new Board("board-1", "Initial", List.of()));

        Board replacement = new Board("board-1", "Updated", List.of());
        repository.save(replacement);

        assertEquals(replacement, repository.findById("board-1").orElseThrow());
    }
}