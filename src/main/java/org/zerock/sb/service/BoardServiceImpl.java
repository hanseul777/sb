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
@RequiredArgsConstructor//자동 주입하려면 필요
public class BoardServiceImpl implements BoardService{

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    @Override
    public Long register(BoardDTO boardDTO) {

        //DTO -> entity
        Board board=modelMapper.map(boardDTO, Board.class);

        //->BoardRepository save() 처리->Long으로 최근 입력한 게시물의 글번호 가져 옴
        boardRepository.save(board);//board가 ID를 가짐

        return board.getBno();//바로 Bno를 불러 올 수 있음
    }

    @Override
    public PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO) {

        char[] typeArr=pageRequestDTO.getTypes();
        String keyword= pageRequestDTO.getKeyword();
        Pageable pageable= PageRequest.of(
                pageRequestDTO.getPage()-1,
                pageRequestDTO.getSize(),
                Sort.by("bno").descending());
        Page<Board> result=boardRepository.search1(typeArr, keyword, pageable);
        List<BoardDTO> dtoList=result.get().map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
        //board를 BoardDTO로 변환해서 리스트에 담음

        long totalCount=result.getTotalElements();

        return new PageResponseDTO<>(pageRequestDTO, (int)totalCount, dtoList);
        //PageResponseDTO에서 int count로 선언해서 long totalCount를 int로 다운 캐스팅함
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

        Optional<Board> result=boardRepository.findById(bno);

        if(result.isEmpty()) {//ID가 없으면 예외 처리: 제대로 하려면 exception 패키지 만들어서 해야 함
            throw new RuntimeException("NOT FOUND");
        }

        return modelMapper.map(result.get(), BoardDTO.class);//board를 boardDTO로 변환
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        Optional<Board> result=boardRepository.findById(boardDTO.getBno());

        //예외를 어떻게 던지는가도 설계의 영역이다
        if(result.isEmpty()) {//수정할 게 없으면 예외 던지게 처리
            throw new RuntimeException("NOT FOUND");
            //예외 상황도 다양해서 설계할 때부터 제대로 해야 함, 예외를 리턴 타입처럼 사용함, 예외는 일종의 메세지
        }

        Board board = result.get();
        board.change(boardDTO.getTitle(), boardDTO.getContent());//제목과 내용을 바꿈(DTO 내용을 가져 와서 board 내용에 넣음)
        boardRepository.save(board);//바뀐 내용을 저장

    }

    @Override
    public void remove(Long bno) {
        boardRepository.deleteById(bno);
    }
}
