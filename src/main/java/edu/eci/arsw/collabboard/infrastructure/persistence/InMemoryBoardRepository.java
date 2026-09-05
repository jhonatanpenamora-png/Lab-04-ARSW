package edu.eci.arsw.collabboard.infrastructure.persistence;

import edu.eci.arsw.collabboard.application.port.out.BoardRepository;
import edu.eci.arsw.collabboard.domain.model.Board;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryBoardRepository implements BoardRepository {

    /*
     * Intentionally simple for Lab 04.
     * Thread-safety is NOT the focus of this lab. Do not redesign this yet only
     * because you remember concurrency from previous weeks; that concern will
     * return in a later evolution of the same application.
     */
    private final Map<String, Board> boards = new HashMap<>();

    @Override
    public Board save(Board board) {
        // Map.put provides the save-or-replace semantics required by the port.
        boards.put(board.id(), board);
        return board;
    }

    @Override
    public Optional<Board> findById(String boardId) {
        // Board is an immutable record and defensively copies its elements list.
        return Optional.ofNullable(boards.get(boardId));
    }

    @Override
    public boolean existsById(String boardId) {
        return boards.containsKey(boardId);
    }
}
