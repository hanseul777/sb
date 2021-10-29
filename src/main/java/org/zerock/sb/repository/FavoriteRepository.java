package org.zerock.sb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.sb.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
