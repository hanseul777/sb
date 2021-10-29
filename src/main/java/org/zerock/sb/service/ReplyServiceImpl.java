package org.zerock.sb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.sb.dto.PageRequestDTO;
import org.zerock.sb.dto.PageResponseDTO;
import org.zerock.sb.dto.ReplyDTO;
import org.zerock.sb.entity.Board;
import org.zerock.sb.entity.Reply;
import org.zerock.sb.repository.ReplyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ModelMapper modelMapper;
    private final ReplyRepository replyRepository;

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {
        Pageable pageable=null;
        if(pageRequestDTO.getPage()==-1) {
            //pageable=calcLastPage(bno);
            int lastPage=calcLastPage(bno, pageRequestDTO.getSize());//댓글 없으면 -1 나옴, 댓글 있으면 마지막 댓글 페이지가 나옴
            if(lastPage<=0) {
                lastPage=1;
            }
            pageRequestDTO.setPage(lastPage);//-1일 때 1로 바꿔 줌
        }

        pageable=PageRequest.of(pageRequestDTO.getPage()-1, pageRequestDTO.getSize());

        Page<Reply> result = replyRepository.getListByBno(bno, pageable);

        List<ReplyDTO> dtoList = result.get()
                .map(reply -> modelMapper.map(reply, ReplyDTO.class))
                .collect(Collectors.toList());

        //dtoList.forEach(replyDTO -> log.info(replyDTO));

        return new PageResponseDTO<>(pageRequestDTO, (int)result.getTotalElements(), dtoList);
    }

    @Override
    public Long register(ReplyDTO replyDTO) {

        //Board board=Board.builder().bno(replyDTO.getBno()).build();
        Reply reply=modelMapper.map(replyDTO, Reply.class); //replyDTO를 reply로 변환
        //log.info(reply); //현재 board는 null: Long 타입의 bno밖에 없어서 변환이 안 될 것
        //log.info(reply.getBoard());
        replyRepository.save(reply);
        return reply.getRno();
    }

    @Override
    public PageResponseDTO<ReplyDTO> remove(Long bno, Long rno, PageRequestDTO pageRequestDTO) {
        replyRepository.deleteById(rno);
        return getListOfBoard(bno, pageRequestDTO);
    }

    @Override
    public PageResponseDTO<ReplyDTO> modify(ReplyDTO replyDTO, PageRequestDTO pageRequestDTO) {
        Reply reply = replyRepository.findById(replyDTO.getRno()).orElseThrow();
        reply.setText(replyDTO.getReplyText());
        replyRepository.save(reply);
        return getListOfBoard(replyDTO.getBno(), pageRequestDTO);
    }

    //private Pageable calcLastPage(Long bno) {
    private int calcLastPage(Long bno, double size) {
        //특정 게시글의 댓글 총 개수를 가지고 마지막 페이지 계산하는 메소드
        int count = replyRepository.getReplyCountOfBoard(bno);
        int lastPage = (int) (Math.ceil(count / size));

//        if(lastPage==0) {//댓글이 0일 때 마지막 페이지 값에 1을 주면 아래에서 1 빼면 0이 나와서 문제 안 생김
//            lastPage=1;
//        }
        //return PageRequest.of(lastPage-1, 10);
        return lastPage;
    }
}
