package org.zerock.sb.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiarySearch {

    Page<Object[]> getSearchList(Pageable pageable);
}
