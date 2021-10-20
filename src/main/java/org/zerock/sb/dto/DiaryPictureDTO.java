package org.zerock.sb.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "uuid") //uuid가 같다면 같은 데이터로 간주한다
public class DiaryPictureDTO {

    private String uuid;
    private String fileName;
    private String savePath;
    private int idx;

    public String getLink(){
        return savePath + "/s" + uuid + "_" + fileName;
    }
}
