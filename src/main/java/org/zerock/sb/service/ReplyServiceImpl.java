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

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() -1, pageRequestDTO.getSize(),
                Sort.by("rno").descending());

        Page<Reply> result = replyRepository.getListByBno(bno, pageable);

        List<ReplyDTO> dtoList = result.get()
                .map(reply -> modelMapper.map(reply, ReplyDTO.class))
                .collect(Collectors.toList());

        //dtoList.forEach(replyDTO -> log.info(replyDTO));

        return new PageResponseDTO<>(pageRequestDTO, (int)result.getTotalElements(), dtoList);
    }
}
