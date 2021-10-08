package org.zerock.sb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.sb.entity.Reply;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    //쿼리메서드를 이용해서 특정 게시글의 댓글을 조회하기
    List<Reply> findReplyByBoard_BnoOrderByRno(Long bno);
}
