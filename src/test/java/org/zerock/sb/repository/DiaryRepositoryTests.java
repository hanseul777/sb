package org.zerock.sb.repository;

import com.google.common.collect.Sets;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.sb.dto.DiaryDTO;
import org.zerock.sb.entity.Diary;
import org.zerock.sb.entity.DiaryPicture;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class DiaryRepositoryTests {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Test
    public void testInsert() {

        IntStream.rangeClosed(1,100).forEach(i -> {

            //태그 추가
            Set<String> tags = IntStream.rangeClosed(1,3).mapToObj(j -> i+"_tag_"+j).collect(Collectors.toSet());

            //첨부파일 추가
            Set<DiaryPicture> pictures = IntStream.rangeClosed(1,3).mapToObj(j -> {
                DiaryPicture picture = DiaryPicture.builder()
                        .uuid(UUID.randomUUID().toString())
                        .savePath("2021/10/18")
                        .fileName("img"+j+".jpg")
                        .idx(j)
                        .build();
                return picture;
            }).collect(Collectors.toSet());

            Diary diary = Diary.builder()
                    .title("sample.."+i)
                    .content("sampl..." + i)
                    .writer("user00")
                    .tags(tags)
                    .pictures(pictures)
                    .build();

            diaryRepository.save(diary);

        });

    }

    //@Transactional //diary.getTags를 가져오기 위해서(ToString에 exclude해놔서 원래 tags는 가지고 오지 않는다) -> transactional을 걸면 db에 여러번접근하는 것
    @Test
    public void testSelectOne(){

        Long dno = 1L;

        Optional<Diary> optionalDiary = diaryRepository.findById(dno);

        //연관관계가 있을 때는 optional을 걸어줘야한다.
        Diary diary = optionalDiary.orElseThrow();

        log.info(diary);

        log.info(diary.getTags());

        log.info(diary.getPictures());
    }

    @Transactional
    @Test
    public void testPaging1(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("dno").descending());

        Page<Diary> result = diaryRepository.findAll(pageable);

        result.get().forEach(diary -> {
            log.info(diary);
            log.info(diary.getTags());//문제가 발생하는 순간 : 태그를 가지고올 때 select를 사용해야만 하는 것
            log.info("--------------------");
        });
    }

    @Test
    public void testSelectOne2(){

        Long dno = 1L;

        Optional<Diary> optionalDiary = diaryRepository.findById(dno);

        //연관관계가 있을 때는 optional을 걸어줘야한다.
        Diary diary = optionalDiary.orElseThrow();

        DiaryDTO dto = modelMapper.map(diary, DiaryDTO.class);

        log.info(dto);
    }

    @Test
    public void testSearchTag(){
        String tag = "1"; //tag가 1인 게시글을 검색
        Pageable pageable = PageRequest.of(0,10,Sort.by("dno").descending());

        Page<Diary> result = diaryRepository.searchTags(tag,pageable);

        result.get().forEach(diary -> {
            log.info(diary);
            log.info(diary.getTags());
            log.info(diary.getPictures());
            log.info("=============================");
        });
    }

    @Test
    public void testDelete(){
        Long dno = 204L;

        diaryRepository.deleteById(dno);
    }

    @Commit
    @Transactional
    @Test
    public void testUpdate(){

        Set<String> updateTags = Sets.newHashSet("aaa","bbb","ccc");

        Set<DiaryPicture> updatePictures = IntStream.rangeClosed(10,15).mapToObj(i -> {
            DiaryPicture picture = DiaryPicture.builder()
                    .uuid(UUID.randomUUID().toString())
                    .savePath("2021/10/20")
                    .fileName("Test" + i + ".jpg")
                    .idx(i)
                    .build();

            return picture;

        }).collect(Collectors.toSet());

        Optional<Diary> optionalDiary = diaryRepository.findById(103L);

        Diary diary = optionalDiary.orElseThrow();

        diary.setTitle("Updated title 103");
        diary.setContent("Update content 103");
        diary.setTags(updateTags);
        diary.setPictures(updatePictures);

        diaryRepository.save(diary);
    }

    @Test
    public void testWithFavorite(){

        Pageable pageable = PageRequest.of(0,10,Sort.by("dno").descending());
        Page<Object[]> result = diaryRepository.findWithFavoriteCount(pageable);

        for (Object[] objects : result.getContent()){
            log.info(Arrays.toString(objects));
        }
    }

    @Test
    public void testListHard(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("dno").descending());

        diaryRepository.getSearchList(pageable);
    }
}
