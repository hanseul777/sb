package org.zerock.sb.entity;

import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_diary")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"tags", "pictures"})
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dno;

    private String title;

    private String content;

    private String writer;

    @CreationTimestamp//시간이 자동으로 관리(Hibernate 기능)
    private LocalDateTime regDate;

    @UpdateTimestamp//시간이 자동으로 관리(Hibernate 기능)
    private LocalDateTime modDate;

    @ElementCollection(fetch = FetchType.LAZY)//종속적인관계에서 사용
    @CollectionTable(name = "tbl_diary_tag")
    @Fetch(value = FetchMode.JOIN) // join : 태그조회하는 쿼리가 날아가기는 하는데, 페이징처리도 되고 N+1 문제도 해결할 수 있다.
    @BatchSize(size = 50)
    //private List<String> tags; //list는 중복적인 데이터가 발생할 수 있어서 list로 설정
    @Builder.Default
    private Set<String> tags=new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY) // 종속관계에서 사용
    @CollectionTable(name = "tbl_diary_picture")
    @Fetch(value = FetchMode.JOIN)
    @BatchSize(size = 50)
    private Set<DiaryPicture> pictures;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void setPictures(Set<DiaryPicture> pictures) {
        this.pictures = pictures;
    }
}