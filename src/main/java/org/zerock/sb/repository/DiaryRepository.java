package org.zerock.sb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.sb.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
