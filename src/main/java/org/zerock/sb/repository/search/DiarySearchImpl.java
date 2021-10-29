package org.zerock.sb.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.sb.entity.Diary;
import org.zerock.sb.entity.QDiary;
import org.zerock.sb.entity.QDiaryPicture;
import org.zerock.sb.entity.QFavorite;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class DiarySearchImpl extends QuerydslRepositorySupport implements DiarySearch{

    public DiarySearchImpl() {
        super(Diary.class);
    }

    @Override
    public Page<Object[]> getSearchList(Pageable pageable) {
        log.info("getSearchList........");
        QDiary qDiary=QDiary.diary;//entity는 이렇게 씀, 값 객체는 쓰는 방법이 어려움
        QFavorite qFavorite=QFavorite.favorite;
        //entity가 아니어서 new를 사용
        QDiaryPicture qDiaryPicture = new QDiaryPicture("pic");


        JPQLQuery<Diary> query=from(qDiary);
        //query.leftJoin(qDiary.tags);//join 처리
        query.leftJoin(qDiary.pictures, qDiaryPicture);
        query.leftJoin(qFavorite).on(qFavorite.diary.eq(qDiary));

        query.where(qDiaryPicture.idx.eq(0));//idx가 0인 사진만 가져 오게
        query.groupBy(qDiary);//qDiary 기준으로 묶음

        //query.select(qDiary.dno, qDiary.title, qDiaryPicture, qDiary.tags.any(), qFavorite.score.sum());
        JPQLQuery<Tuple> tupleJPQLQuery=query.select(qDiary.dno, qDiary.title, qDiaryPicture, qFavorite.score.sum());

        getQuerydsl().applyPagination(pageable, tupleJPQLQuery);

        log.info("query: "+query);

        //List<Diary> diaryList=query.fetch();
        //query.fetch();

        List<Tuple> tupleList=tupleJPQLQuery.fetch();
        long count=tupleJPQLQuery.fetchCount();

        return new PageImpl<>(tupleList.stream().map(tuple -> tuple.toArray()).collect(Collectors.toList()),pageable, count);
    }
}
