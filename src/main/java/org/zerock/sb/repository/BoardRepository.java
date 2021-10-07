package org.zerock.sb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.sb.entity.Board;
import org.zerock.sb.repository.search.BoardSearch;

public interface BoardRepository extends JpaRepository<Board,Long>, BoardSearch {
}
