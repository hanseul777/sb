package org.zerock.sb.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Board {

    @Id //Entity는 Id를 기준으로 돌아가기 때문에 Id는 변경하면 x
    @GeneratedValue(strategy = GenerationType.IDENTITY) //만들어지는 value값을 사용하고(T생성전략은 어떤거를 쓸건지 설정하는 것)
    private Long bno;

    private String title;

    private String content;

    private String writer;

    @CreationTimestamp//시간이 자동으로 관리(Hibernate 기능)
    private LocalDateTime regDate;

    @UpdateTimestamp//시간이 자동으로 관리(Hibernate 기능)
    private LocalDateTime modDate;

    //제목과 내용을 한 번에 갱신, 수정 할 수 있는 메서드를 생성
    public void change(String title, String content) {
        this.title=title;
        this.content=content;
    }

}













