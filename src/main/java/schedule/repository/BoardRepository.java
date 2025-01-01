package schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schedule.model.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}