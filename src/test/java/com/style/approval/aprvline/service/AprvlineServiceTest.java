package com.style.approval.aprvline.service;

import com.style.approval.aprvdoc.dto.AprvdocDto;
import com.style.approval.aprvdoc.service.AprvdocService;
import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.aprvline.entity.AprvlineEntity;
import com.style.approval.aprvline.repository.AprvlineRepository;
import com.style.approval.auth.dto.SignupDto;
import com.style.approval.auth.service.AuthService;
import com.style.approval.enums.AprvStatus;
import com.style.approval.enums.DocStatus;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AprvlineServiceTest {

    @Autowired
    AprvlineService aprvlineService;

    @Autowired
    AprvlineRepository aprvlineRepository;

    @Autowired
    AprvdocService aprvdocService;

    @Autowired
    AuthService authService;

    @BeforeEach
    void signUp() {
        SignupDto.Request signupDto = new SignupDto.Request().builder()
                .email("test@naver.com")
                .password("1234")
                .username("tester")
                .role("USER")
                .build();

        authService.signup(signupDto);
    }

    @BeforeEach
    void addAprvdoc() {
        List<AprvlineDto.Request> aprvlineDtoList = new ArrayList<>();
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo("2022-11-11")
                .seqNo(1L)
                .status(AprvStatus.APRV_REQ.getCode())
                .username("tester")
                .build();

        aprvlineDtoList.add(aprvlineDto);

        AprvdocDto.Request aprvdocDto = new AprvdocDto.Request().builder()
                .docNo("2022-11-11")
                .title("홍길동 - 휴가신청서")
                .category("휴가신청서")
                .username("tester")
                .aprvlineList(aprvlineDtoList)
                .aprvOrder("홍길동^김길동^박길동")
                .status(DocStatus.ACCEPT.getCode())
                .build();

        aprvdocService.addAprvdoc(aprvdocDto);
    }


    @Test
    @DisplayName("결재라인조회 - 문서번호")
    @Order(1)
    void findAprvlineByDocNo() {
        //given
        String docNo = "2022-11-11";
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo(docNo)
                .build();

        //when
        List<AprvlineDto.Response> result = aprvlineService.findAllByDocNo(aprvlineDto);

        //then
        Assertions.assertEquals(docNo, result.get(0).getDocNo());
    }

    @Test
    @DisplayName("결재라인조회 - 문서번호 & 결재자")
    @Order(2)
    void findByDocNoAndAprvUser() {
        //given
        String docNo = "2022-11-11";
        String aprvUser = "tester";
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo(docNo)
                .username(aprvUser)
                .build();

        //when
        AprvlineDto.Response result = aprvlineService.findByDocNoAndAprvUser(aprvlineDto);

        //then
        Assertions.assertEquals(docNo, result.getDocNo());
        Assertions.assertEquals(aprvUser, result.getUsername());
    }

    @Test
    @DisplayName("결재라인저장")
    @Order(3)
    void addAprvline() {
        //given
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo("2022-11-11")
                .seqNo(1L)
                .status(AprvStatus.APRV_REQ.getCode())
                .username("tester")
                .build();

        //when
        boolean result = aprvlineService.addAprvline(aprvlineDto);
        //then
        Assertions.assertEquals(true, result);
    }


    @Test
    @DisplayName("결재라인수정")
    @Order(4)
    void modifyAprvline() {
        //given
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo("2022-11-11")
                .username("tester")
                .status(AprvStatus.APRV_REJECT.getCode())
                .username("tester")
                .build();

        //when
        boolean result = aprvlineService.modifyAprvline(aprvlineDto);

        //then
        Assertions.assertEquals(true, result);
    }

    @Test
    @DisplayName("결재승인")
    @Order(5)
    void acceptAprvline() {
        //given
        String docNo = "2022-11-11";
        Long seqNo = 1L;
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo(docNo)
                .seqNo(seqNo)
                .username("tester")
                .build();

        //when
        aprvlineService.acceptAprvline(aprvlineDto);

        //then
        AprvlineEntity aprvline = aprvlineRepository.findByDocNoAndSeqNo(docNo, seqNo).orElseThrow(() -> new ServiceException("일치하는 문서 없음."));
        Assertions.assertEquals(AprvStatus.APRV_ACCEPT.getCode(), aprvline.getStatus());
    }

    @Test
    @DisplayName("결재기각")
    @Order(6)
    void rejectAprvline() {
        //given
        String docNo = "2022-11-11";
        Long seqNo = 1L;
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo(docNo)
                .seqNo(seqNo)
                .username("tester")
                .build();

        //when
        aprvlineService.rejectAprvline(aprvlineDto);

        //then
        AprvlineEntity aprvline = aprvlineRepository.findByDocNoAndSeqNo(docNo, seqNo).orElseThrow(() -> new ServiceException("일치하는 문서 없음."));
        Assertions.assertEquals(AprvStatus.APRV_REJECT.getCode(), aprvline.getStatus());
    }

}