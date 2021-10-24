package org.zerock.sb.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.sb.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    @EntityGraph(attributePaths = "roleSet") //어떤애("roleSet")까지 같이 로딩할까요? 필요할 때마다 지정(attributePaths)이 가능하다
    @Query("select m from Member m where m.mid = :mid")
    Optional<Member> getMemberEager(String mid);

}