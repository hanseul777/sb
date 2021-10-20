package org.zerock.sb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.sb.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

}