package org.zerock.sb.repository.search;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.sb.entity.Board;
import org.zerock.sb.entity.QBoard;

import javax.persistence.Query;
import java.util.List;

@Log4j2
public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{

    public BoardSearchImpl() {
        super(Board.class); //동적쿼리를 사용하고 싶은 entity를 지정해준다.
    }

    @Override
    public Page<Board> search1(char[] typeArr, String keyword, Pageable pageable) {
        log.info("------------search1");

//        log.info(this.getQuerydsl().createQuery(QBoard.board).fetch());

//        Query query = this.getEntityManager().createQuery("select b from Board b order by b.bno desc ");
//
//        log.info(query.getResultList());

        QBoard board = QBoard.board;

        JPQLQuery<Board> jpqlQuery = from(board); //from은 부모쪽에 있는 메서드

        //검색조건이 있다면
        if(typeArr != null && typeArr.length > 0){

            //th블럭같은 역학을 한다. 안에 들어가는 애들은 true, false로 나오는 것만 사용해야한다.
            BooleanBuilder condition = new BooleanBuilder(); //괄호열고 괄호닫고 같은 기능

            for(char type: typeArr){
                if(type == 'T'){
                    //jpalQuery.where(board.title.contains(keyword)); => and로 연결
                    condition.or(board.title.contains(keyword));
                }else if(type =='C'){
                    condition.or(board.content.contains(keyword));
                }else if(type == 'W'){
                    condition.or(board.writer.contains(keyword));
                }
            }
            jpqlQuery.where(condition);
        }

        jpqlQuery.where(board.bno.gt(0L)); //PK타도록 bno>0인 조건을 추가해준다.

        //페이징쿼리 만들기 (Querydsl이 pageable사용해서 자동으로 쿼리를 생성해 주는 것) -> limit, order by 걸리게 한다.
        JPQLQuery<Board> pagingQuery =
                this.getQuerydsl().applyPagination(pageable, jpqlQuery);

        //fetch() : 실제로 쿼리를 실행해주는 것
        List<Board> boardList = pagingQuery.fetch();
        long totalCount = pagingQuery.fetchCount();

        return new PageImpl<>(boardList, pageable, totalCount);

    }
}
