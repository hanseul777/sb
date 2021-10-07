package org.zerock.sb.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.sb.entity.Board;

public interface BoardSearch {

    Page<Board> search1(char[] typeArr, String keyword, Pageable pageable);
}
