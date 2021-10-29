package org.zerock.sb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.zerock.sb.entity.Board;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {

    private Long rno;

    private String replyText;

    private String replyer;

    //가져 올 거 Board 아니고 그냥 bno 가져 오면 됨, 틀리면 매핑 안 해 줄 거임 -_-
    private Long bno;

    private LocalDateTime replyDate;

}
