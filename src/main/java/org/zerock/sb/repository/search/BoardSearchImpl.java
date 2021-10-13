package org.zerock.sb.repository.search;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.sb.entity.Board;
import org.zerock.sb.entity.QBoard;
import org.zerock.sb.entity.QReply;

import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Page<Object[]> searchWithReplyCount(char[] typeArr, String keyword, Pageable pageable) {

        log.info("searchWithReplyCount");

        //1. EntityManager()를 이용해서 Query
        //2. getQuerydsl() 을 이용하는 방식

        //Query를 만들때는 Q도메인 -- 값을 뽑을 때는 Entity타입/값을 이용한다.
        //Q도메인은 쿼리를 만들기 위한 객체(Q도메인을 이용해서 sql을 만드는 것)
        QBoard qBoard = QBoard.board;
        QReply qReply = QReply.reply;

        //Board b left join Reply r on r.board.bno = b.bno
        JPQLQuery<Board> query = from(qBoard); //리턴타입 : JPQLQuery<>
        query.leftJoin(qReply).on(qReply.board.eq(qBoard));
        //query.where(qBoard.bno.eq(200L)); //목록페이를 페이징할 때 where -> order by
        //order by의 기능을 주기 위해 applyPagination를 사용한다.
        query.groupBy(qBoard);
        //검색조건이 있다면
        if(typeArr != null && typeArr.length > 0){

            BooleanBuilder condition = new BooleanBuilder();

            for(char type: typeArr){
                if(type == 'T'){
                    condition.or(qBoard.title.contains(keyword));
                }else if(type =='C'){
                    condition.or(qBoard.content.contains(keyword));
                }else if(type == 'W'){
                    condition.or(qBoard.writer.contains(keyword));
                }
            }
            query.where(condition);
        }

        //원하는 값 따로 뽑아서 select -> 튜플
        JPQLQuery<Tuple> selectQuery =
                query.select(qBoard.bno, qBoard.title, qBoard.writer, qBoard.regDate, qReply.count());

        //order by 역할을 하는 부분 : applyPagination
        this.getQuerydsl().applyPagination(pageable, selectQuery);

        log.info(selectQuery);

        List<Tuple> tupleList = selectQuery.fetch();//fetch() 작업이 이루어질 때 진짜 sql작업 실행

        long totalCount = selectQuery.fetchCount();//count는 꼭 long타입으로 주기

        //이차원배열이기 때문에 map.collect 사용해서 변경해줘야한다.
        List<Object[]> arr = tupleList.stream().map(tuple -> tuple.toArray()).collect(Collectors.toList());

        return new PageImpl<>(arr,pageable,totalCount);
    }
}
