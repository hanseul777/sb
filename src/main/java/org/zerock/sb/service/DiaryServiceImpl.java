package org.zerock.sb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.sb.dto.DiaryDTO;
import org.zerock.sb.dto.DiaryListDTO;
import org.zerock.sb.dto.PageRequestDTO;
import org.zerock.sb.dto.PageResponseDTO;
import org.zerock.sb.entity.Diary;
import org.zerock.sb.repository.DiaryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //자동주입
@Log4j2
public class DiaryServiceImpl implements DiaryService{

    private final ModelMapper modelMapper;

    private final DiaryRepository diaryRepository;

    @Override
    public Long register(DiaryDTO dto) {

        Diary diary = modelMapper.map(dto, Diary.class);

        log.info(diary);
        log.info(diary.getTags());
        log.info(diary.getPictures());

        diaryRepository.save(diary);

        return diary.getDno();
    }

    @Override
    public DiaryDTO read(Long dno) {

        Optional<Diary> optionalDiary = diaryRepository.findById(dno);

        Diary diary = optionalDiary.orElseThrow();

        DiaryDTO dto = modelMapper.map(diary, DiaryDTO.class);

        return dto;
    }

    @Override
    public PageResponseDTO<DiaryDTO> getList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage()-1,
                pageRequestDTO.getSize(),
                Sort.by("dno").descending());

        Page<Diary> result = diaryRepository.findAll(pageable);
        long totalCount = result.getTotalElements();

        List<DiaryDTO> dtoList = result.get().map(diary -> modelMapper.map(diary,DiaryDTO.class)).collect(Collectors.toList());

        return new PageResponseDTO<>(pageRequestDTO,(int)totalCount,dtoList);
    }

    //Object[]여서 기존과는 방식이 달라진다
    @Override
    public PageResponseDTO<DiaryListDTO> getListWithFavorite(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("dno").descending());

        Page<Object[]> result = diaryRepository.findWithFavoriteCount(pageable);

        long totalCount = result.getTotalElements();

        //object배열을 DiaryListDTO로 바꿔주기
        List<DiaryListDTO> dtoList = result.get().map(objects -> {
            Object[] arr = (Object[])objects;
            Diary diary = (Diary)arr[0];
            long totalScore = (long)arr[1]; //타입주의하기 int(X)

           //log.info(diary);
           //log.info(totalScore);
           //log.info("=======================");

           //다이어리의 내용을 DiaryDTO로 변환할 때는 ModelMapper를 사용해준다.
           //일단 필요한 애들만 복사
            DiaryListDTO diaryListDTO = modelMapper.map(diary, DiaryListDTO.class);
            diaryListDTO.setTotalScore((int)totalScore); // long타입 -> int

           //log.info(diaryListDTO);
           //log.info("=====================");

           return diaryListDTO;
        }).collect(Collectors.toList());

        return new PageResponseDTO<>(pageRequestDTO, (int)totalCount, dtoList);
    }
}
