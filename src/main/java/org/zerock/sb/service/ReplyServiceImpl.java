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
public class ReplyServiceImpl implements ReplyService{

    private final ModelMapper modelMapper;
    private final ReplyRepository replyRepository;

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {

        Pageable pageable = null;

        if (pageRequestDTO.getPage() == -1){
            int lastPage = calcLastPage(bno, pageRequestDTO.getSize()); //-1 : 댓글이 없는 경우, 숫자 : 마지막 댓글 페이지
            if(lastPage <= 0){ //latsPage ==-1 이라면
                lastPage = 1;
            }
            pageRequestDTO.setPage(lastPage);
        }

        pageable = PageRequest.of(pageRequestDTO.getPage() -1, pageRequestDTO.getSize());

        Page<Reply> result = replyRepository.getListByBno(bno, pageable);

        List<ReplyDTO> dtoList = result.get()
                .map(reply -> modelMapper.map(reply, ReplyDTO.class))
                .collect(Collectors.toList());

        //dtoList.forEach(replyDTO -> log.info(replyDTO));

        return new PageResponseDTO<>(pageRequestDTO, (int)result.getTotalElements(), dtoList);
    }

    private int calcLastPage(Long bno, double size) {
        int count = replyRepository.getReplyCountOfBoard(bno);

        int lastPage = (int)(Math.ceil(count/size));
//
//        if(lastPage ==0){
//            lastPage = 1;
//        }
//        //0부터 시작하는 페이지번호, 사이즈, 소트
//        return PageRequest.of(lastPage -1,10);
        return lastPage;
    }

    @Override
    public Long register(ReplyDTO replyDTO) {

        //entity에 Board타입으로 되어있어서 Board로 만들어줘야 한다.
        Board board = Board.builder().bno(replyDTO.getBno()).build();

        Reply reply = modelMapper.map(replyDTO, Reply.class);
        replyRepository.save(reply);

        return reply.getRno();
    }

    @Override
    public PageResponseDTO<ReplyDTO> remove(Long bno, Long rno, PageRequestDTO pageRequestDTO) {

        replyRepository.deleteById(rno);

        return getListOfBoard(bno, pageRequestDTO);
    }
}
