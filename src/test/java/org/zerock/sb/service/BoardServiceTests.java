package org.zerock.sb.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.sb.dto.BoardDTO;

import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testModify(){
        BoardDTO boardDTO = BoardDTO.builder().bno(201L).title("수정테스트").content("수정").build();

        boardService.modify(boardDTO);
    }

    @Test
    public void testRegister(){

        IntStream.rangeClosed(1,200).forEach(i -> {
            BoardDTO boardDTO = BoardDTO.builder()
                    .title("Title..." + i)
                    .content("Content..." + i)
                    .writer("user..." + (i%10))
                    .build();

            Long bno = boardService.register(boardDTO);
            log.info("BNO: " + bno);
        });
    }
}
