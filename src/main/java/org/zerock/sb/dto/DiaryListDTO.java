package org.zerock.sb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiaryListDTO {

    private Long dno;

    private String title;

    private String writer;

    private LocalDateTime regDate;

    private List<String> tags;
    private List<DiaryPictureDTO> pictures;

    private int totalScore;
}
