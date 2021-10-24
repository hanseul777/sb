package org.zerock.sb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.sb.entity.Diary;
import org.zerock.sb.repository.search.DiarySearch;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiarySearch {

    @Query("select d from Diary d left join d.tags dt where dt like concat('%', :tag, '%') ")
    Page<Diary> searchTags(String tag, Pageable pageable);

    @Query("select d, coalesce( sum(f.score),0) from Diary d left join Favorite f on f.diary = d group by d ")
    Page<Object[]> findWithFavoriteCount(Pageable pageable);
    //coalesce() : null이면 0으로 출력하는 것(if null이면 0으로 출력)
}
