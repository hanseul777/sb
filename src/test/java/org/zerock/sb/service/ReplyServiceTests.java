package org.zerock.sb.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.sb.dto.PageRequestDTO;

@SpringBootTest
@Log4j2
public class ReplyServiceTests {

    @Autowired
    private ReplyService replyService;

    @Test
    public void testList(){
        Long bno = 196L;

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();

        replyService.getListOfBoard(bno, pageRequestDTO);
    }
}
