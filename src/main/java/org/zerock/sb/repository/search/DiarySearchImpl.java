package org.zerock.sb.repository.search;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.sb.entity.Diary;
import org.zerock.sb.entity.QDiary;
import org.zerock.sb.entity.QDiaryPicture;
import org.zerock.sb.entity.QFavorite;

import java.util.List;

@Log4j2
public class DiarySearchImpl extends QuerydslRepositorySupport implements DiarySearch{

    public DiarySearchImpl() {
        super(Diary.class);
    }

    @Override
    public Page<Object[]> getSearchList(Pageable pageable) {

        log.info("=============getSearchList==============");

        QDiary qDiary = QDiary.diary;
        QFavorite qFavorite = QFavorite.favorite;
        //entity가 아니어서 new를 사용
        QDiaryPicture qDiaryPicture = new QDiaryPicture("pic");


        JPQLQuery<Diary> query = from(qDiary);
        query.leftJoin(qDiary.tags); //따로 join처리해줌
        query.leftJoin(qFavorite).on(qFavorite.diary.eq(qDiary));
        query.leftJoin(qDiary.pictures, qDiaryPicture);

        query.groupBy(qDiary);

        query.select(qDiary.dno, qDiary.title, qDiaryPicture, qDiary.tags.any(), qFavorite.score.sum());

        getQuerydsl().applyPagination(pageable, query);

        //쿼리생성확인 , select정상적으로 작동하는지 확인
        log.info("query : "+ query);

        query.fetch();



        return null;
    }
}
