package org.zerock.sb.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dno;

    private String title;

    private String content;

    private String writer;

    @CreationTimestamp //등록 수정시간 자동으로 관리해주기
    private LocalDateTime regDate;

    @UpdateTimestamp
    private LocalDateTime modDate;

    @ElementCollection //종속적인관계에서 사용하는 어노테이션
    private List<String> tags;//list는 중복적인 데이터가 발생할 수 있어서 list로 설정
}
