package org.zerock.sb.entity;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable // elementcollection으로 처리가 가능하도록 하는 어노테이션 -> PK필요 X
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class DiaryPicture implements Comparable<DiaryPicture>{

    private String uuid;
    private String fileName;
    private String savePath;
    private int idx; //1번사진만 가져온다던가 할 때 사용 : 한 페이지를 조회할 때 총 4번의 select(tag까지)가 처리되는게 싫은 경우

    @Override
    public int compareTo(DiaryPicture o) {
        return this.idx - o.idx;
    }
}
