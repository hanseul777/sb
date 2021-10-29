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
@ToString(exclude = "board")
public class Reply {

    @Id//entity이기 때문에 id 안 쓰면 에러 남
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    private String replyText;

    private String replyer;

    //reply가져올 때 board도 가져와야 하고 꼭 lazy(지연로딩 : 단방향으로 주기위해 사용한다.)로 걸어야한다.
    @ManyToOne(fetch = FetchType.LAZY)
    //이걸 안 쓰면 JPA가 게시글과 댓글 관계를 몰라서 에러 남, 설명해 줘야 함
    private Board board;

    @CreationTimestamp
    private LocalDateTime replyDate;

    public void setText(String text){
        this.replyText = text;
    }

}