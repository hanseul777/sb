package org.zerock.sb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.sb.dto.BoardDTO;
import org.zerock.sb.dto.PageRequestDTO;
import org.zerock.sb.dto.PageResponseDTO;
import org.zerock.sb.service.BoardService;

@RestController // 목적이 controller라서 처음부터 restcontroller로 생성
@RequestMapping("/api/")
@RequiredArgsConstructor
@Log4j2
public class ApiController {

    //서비스계층은 연산, 조합, 가공을 진행
    //컨트롤러 계층은 service계층만 봐야한다!
    private final BoardService boardService;

    @GetMapping("/board/list")
    public PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO){

        log.info("pageRequestDTO: " + pageRequestDTO);

        return boardService.getList(pageRequestDTO);
    }
}
