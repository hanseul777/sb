package org.zerock.sb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.sb.dto.BoardDTO;
import org.zerock.sb.dto.BoardListDTO;
import org.zerock.sb.dto.PageRequestDTO;
import org.zerock.sb.dto.PageResponseDTO;
import org.zerock.sb.entity.Board;
import org.zerock.sb.repository.BoardRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor//자동주입

public class BoardServiceImpl implements BoardService{

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    @Override
    public Long register(BoardDTO boardDTO) {

        //DTO -> entity
        Board board = modelMapper.map(boardDTO, Board.class);
        //repository save() -> Long
        boardRepository.save(board);

        return board.getBno();
    }

    @Override
    public PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO) {

        char[] typeArr = pageRequestDTO.getTypes(); //검색조건
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage()-1,
                pageRequestDTO.getSize(),
                Sort.by("bno").descending());
        //PageRequestDTO.getPage는 1부터 시작인데 pageable은 0부터 시작이라서 -1

        Page<Board> result = boardRepository.search1(typeArr,keyword,pageable);

        //entity객체는 전달안해줄거임 -> dto로 전달해주는 기능이 필요
        List<BoardDTO> dtoList = result.get().map(board -> modelMapper.map(board,BoardDTO.class)).collect(Collectors.toList());
        long totalCount = result.getTotalElements();

        return new PageResponseDTO<>(pageRequestDTO,(int)totalCount,dtoList);
    }

    @Override
    public PageResponseDTO<BoardListDTO> getListWithReplyCount(PageRequestDTO pageRequestDTO) {

        char[] typeArr = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("bno").descending());

        Page<Object[]> result = boardRepository.searchWithReplyCount(typeArr, keyword, pageable);

        List<BoardListDTO> dtoList = result.get().map(objects -> {
            BoardListDTO listDTO = BoardListDTO.builder()
                    .bno((Long)objects[0])
                    .title((String)objects[1])
                    .writer((String)objects[2])
                    .regDate((LocalDateTime) objects[3])
                    .replyCount((Long)objects[4])
                    .build();
            return listDTO;
        }).collect(Collectors.toList());

        return new PageResponseDTO<>(pageRequestDTO, (int)result.getTotalElements(), dtoList);
    }

    @Override
    public BoardDTO read(Long bno) {
        Optional<Board> result = boardRepository.findById(bno);

        if (result.isEmpty()){
            throw new RuntimeException("NOT FOUND");
        }

        //내용물을 dto로 변경해주기
        BoardDTO dto = modelMapper.map(result.get(), BoardDTO.class);

        //매핑된 결과를 반환해준다.
        return dto;
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        Optional<Board> result = boardRepository.findById(boardDTO.getBno());

        //예외를 어떻게 던지는가도 설계의 영역이다
        if (result.isEmpty()){
            throw new RuntimeException("NOT FOUND");
        }

        Board board = result.get();
        board.change(boardDTO.getTitle(), boardDTO.getContent());
        boardRepository.save(board);

    }

    @Override
    public void remove(Long bno) {

        boardRepository.deleteById(bno);
    }
}
