package org.zerock.sb.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.sb.entity.Board;

public interface BoardSearch {

    //Pageable을 파라미터에서 사용하면 리턴타입은 꼭 Page로 주기
    Page<Board> search1(char[] typeArr, String keyword, Pageable pageable);
}
