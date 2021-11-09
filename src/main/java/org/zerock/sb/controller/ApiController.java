package org.zerock.sb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.zerock.sb.dto.BoardDTO;
import org.zerock.sb.dto.PageRequestDTO;
import org.zerock.sb.dto.PageResponseDTO;
import org.zerock.sb.service.BoardService;

import java.util.Map;

// 목적이 controller라서 처음부터 restcontroller로 생성
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class ApiController {

    //서비스계층은 연산, 조합, 가공을 진행
    //컨트롤러 계층은 service계층만 봐야한다!
    private final BoardService boardService;

    @PostMapping("/board/register")
    public Map<String, Long> register(@RequestBody BoardDTO boardDTO){
        log.info("......................." + boardDTO);

        Long bno = boardService.register(boardDTO); //bno번 게시글이 등록되었다고 나오는 것

        return Map.of("result", bno);
    }

    @GetMapping("/board/list")
    public PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO) {
        log.info("pageRequestDTO: "+ pageRequestDTO);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return boardService.getList(pageRequestDTO);
    }
}
