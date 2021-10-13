package org.zerock.sb.service;

import org.springframework.transaction.annotation.Transactional;
import org.zerock.sb.dto.BoardDTO;
import org.zerock.sb.dto.BoardListDTO;
import org.zerock.sb.dto.PageRequestDTO;
import org.zerock.sb.dto.PageResponseDTO;

@Transactional
public interface BoardService {

    Long register(BoardDTO boardDTO);

    PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO);

    PageResponseDTO<BoardListDTO> getListWithReplyCount(PageRequestDTO pageRequestDTO);

    BoardDTO read(Long bno);

    void modify(BoardDTO boardDTO);

    void remove(Long bno);
}
