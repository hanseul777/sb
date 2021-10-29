package org.zerock.sb.entity;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable // elementcollection으로 처리가 가능하도록 하는 어노테이션 -> PK필요 X
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "uuid")
public class DiaryPicture implements Comparable<DiaryPicture>{

    private String uuid;
    private String fileName;
    private String savePath;
    private int idx; //첨부한 파일의 번호(게시글 1개에 파일 여러 개 첨부할 때 사용): 한 페이지를 조회할 때 총 4번의 select(tag까지)가 처리되는게 싫은 경우

    @Override
    public int compareTo(DiaryPicture o) {
        return this.idx - o.idx;
    }
}
