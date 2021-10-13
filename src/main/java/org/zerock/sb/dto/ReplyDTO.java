package org.zerock.sb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {

    private Long rno;

    private String replyText;

    private String replyer;

    //가져올꺼 Board아니고 그냥 bno 가져오면됨 틀리면 매핑안해줄거임 -_-
    private Long bno;

    private LocalDateTime replyDate;
}
