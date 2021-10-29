package org.zerock.sb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.sb.entity.Reply;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findReplyByBoard_BnoOrderByRno(Long bno);
    //쿼리 메소드로 만드는 법: board bno 값으로 해당 게시글의 댓글 목록 불러 오기

    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> getListByBno(Long bno, Pageable pageable);

    //정석은 Long
    @Query("select count(r) from Reply r where r.board.bno=:bno")
    int getReplyCountOfBoard(Long bno);
}
