package org.zerock.sb.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.sb.entity.Board;
import org.zerock.sb.entity.Reply;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void insert200(){
        IntStream.rangeClosed(1,200).forEach(i -> {
            Long bno = (long)200 - (i % 5); //200,199,198,197,196

            int replyCount = (i % 5); //0,1,2,3,4

            //보드객체를 만들어서 그 bno를 reply에 넣어줘야햔다.(id가 있으면 식별이 가능)
            Board board = Board.builder().bno(bno).build();

            IntStream.rangeClosed(0,replyCount).forEach(j ->{
                Reply reply = Reply.builder()
                        .replyText("Reply...")
                        .replyer("replyer...")
                        .board(board)
                        .build();

                replyRepository.save(reply);
            });//inner loop

        });//outer loop
    }

    @Transactional
    @Test
    public void testRead(){
        Long rn = 1L;

        Reply reply = replyRepository.findById(rn).get();

        log.info(reply);

        log.info(reply.getBoard());
    }
    //테스트를 진행해보면 noSession이라고 뜬다
    // 실행 후 쿼리문을 보면 reply테이블을 뒤져서 reply의 ToString만 가지고 오는 것을 확인이 가능하다.
    // board의 ToString도 가지고 와야하는데 lazy상태여서 board는 가지고 오지 못한다. -> ToString 예외 걸어주기

    @Test
    public void testByBno(){
        Long bno = 200L;

        List<Reply> replyList
                = replyRepository.findReplyByBoard_BnoOrderByRno(bno);

        replyList.forEach(reply -> log.info(reply));
    }

    @Test
    public void testListOfBoard(){
        Pageable pageable =
                PageRequest.of(0,10, Sort.by("rno").descending());

        Page<Reply> result = replyRepository.getListByBno(197L,pageable);

        log.info(result.getTotalElements());

        result.get().forEach(reply -> log.info(reply));//화면이 여러개라서 forEach
    }

    @Test
    public void testCountOfBoard(){
        Long bno = 198L;

        int count = replyRepository.getReplyCountOfBoard(bno);
        int lastPage = (int)(Math.ceil(count/10.0));

        //121/10.0 = 13 => 13*10 = 130 => limit 110,120
        //한페이지당 10개씩 뿌린다고 가정
//        int lastpageNum = ((int)Math.ceil(count / (double)10));
////        log.info(lastpageNum); //12
//
//        int lastEnd = lastpageNum * 10;
//        int lastStart = lastEnd - 10;
//        log.info(lastStart + " : " + lastEnd);

        //Pageable pageable = PageRequest.of(lastPage<=0?0: lastPage -1,10);
        if(lastPage ==0){
            lastPage = 1;
        }

        //of()안에는 0부터 시작하는 페이지 번호, 사이즈, 소트가 들어간다.
        Pageable pageable = PageRequest.of(lastPage -1,10);

        Page<Reply> result = replyRepository.getListByBno(bno,pageable); //해당게시글의 댓글의 목록을 가지고와야해서 findAll사용 X

        log.info("total : " + result.getTotalElements());
        log.info("====="+result.getTotalPages());

        result.get().forEach(reply -> {
            log.info(reply);
        });
    }
}
