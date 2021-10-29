package org.zerock.sb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.sb.entity.Board;
import org.zerock.sb.repository.search.BoardSearch;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    //페이지처리를 하면서 댓글의 갯수를 가지고 올 수 있게 쿼리문을 만들기 -> join과 count(집합함수 : group by)를 사용
    //select다음에 ,사용해서 여러개를 가져오려면 Object의 배열로만 받을 수 있다.
    // select가 하나이상이면 꼭 Object배열로 뽑아야한다.
    @Query("select b.bno, b.title, b.writer, count(r) from Board b left join Reply r on r.board = b group by b")
    Page<Object[]> ex1(Pageable pageable); //페이징처리를 해야하기 때문에 Pageable -> 리턴타입 Page
}
