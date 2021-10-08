package org.zerock.sb.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "board")//연관관계를 걸어줄 때는 ToString은 제외를 걸어줘야 한다!
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    private String replyText;

    private String replyer;

    //reply가져올 때 board도 가져와야 하고 꼭 lazy(지연로딩 : 단방향으로 주기위해 사용한다.)로 걸어야한다.
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @CreationTimestamp
    private LocalDateTime replyDate;

}