package edu.eci.arsw.collabboard.application.port.out;

import edu.eci.arsw.collabboard.domain.model.Board;

import java.util.Optional;

/**
 * Output port owned by the application boundary. Its operations are the
 * minimum required by the create, get and replace use cases.
 */
public interface BoardRepository {
    Board save(Board board);

    Optional<Board> findById(String boardId);

    boolean existsById(String boardId);
}
