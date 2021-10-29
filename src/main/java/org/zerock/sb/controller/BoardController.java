package org.zerock.sb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.sb.dto.BoardDTO;
import org.zerock.sb.dto.PageRequestDTO;
import org.zerock.sb.service.BoardService;

@Controller//알아서 bean을 인식하니까 별도로 안 만들어도 됨
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    //(value = {"","/list"})//value 쓰면 board로 들어 와도 list가 나옴
    public void list(PageRequestDTO pageRequestDTO, Model model) {

        //model.addAttribute("responseDTO", boardService.getList(pageRequestDTO));
        model.addAttribute("responseDTO", boardService.getListWithReplyCount(pageRequestDTO));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/register")
    public void register() {
    }

    @PostMapping("/register")
    public String registerPost(BoardDTO boardDTO, RedirectAttributes redirectAttributes) {
    //redirect가 문자열이니까 String으로 써야 함

        Long bno=boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);

        return "redirect:/board/list";
    }

    @GetMapping("/read")
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model) {
        //list로 돌아가기 위해서는 page 정보를 갖고 있어야 해서 PageRequestDTO도 필요

        model.addAttribute("dto", boardService.read(bno));
    }
}
