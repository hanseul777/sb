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
@ToString(exclude = {"diary","memeber"})
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long fno;

    @ManyToOne(fetch = FetchType.LAZY)
    private Diary diary;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private int score;

    @CreationTimestamp
    private LocalDateTime regDate;
}
