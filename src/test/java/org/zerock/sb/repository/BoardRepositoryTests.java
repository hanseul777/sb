package org.zerock.sb.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.sb.dto.BoardDTO;
import org.zerock.sb.entity.Board;

import java.util.Arrays;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testSearch1(){
        char[] typeArr = null;
        String keyword = null;
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.search1(typeArr,keyword,pageable);

        result.get().forEach(board -> {
            log.info(board);
            log.info("============================");

            //modelMapper확인하기 -> 실행해보면 자동으로 boardDTO로 변한 것을 확인이 가능하다.
            BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

            log.info(boardDTO);
        });
    }

    @Test
    public void testEx1(){
        //쿼리문 확인하는 테스트
        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        Page<Object[]> result = boardRepository.ex1(pageable);

        log.info(result);

        //결과확인해보는 테스트
        result.get().forEach(element-> {

            //element를 Object[]로 다운케스팅 (result안의 내용이 Object[]이기 때문에)
            Object[] arr = (Object[])element;

            //다차원배열
            //arr자체가 배열이고 그 안의 내용물도 배열
            //(select로 생성 된 Object의 배열(a)안의 배열(b)이 생기는데, b안에 board와 reply의 count가 들어가 있다)
            log.info(Arrays.toString(arr));
        });

    }
}
