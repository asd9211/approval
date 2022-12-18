package com.style.approval.aprvdoc.service;

import com.style.approval.aprvdoc.dto.AprvdocDto;
import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.aprvline.service.AprvlineService;
import com.style.approval.auth.dto.SignupDto;
import com.style.approval.auth.service.AuthService;
import com.style.approval.enums.AprvStatus;
import com.style.approval.enums.DocStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class AprvdocServiceTest {

    @Autowired
    AprvdocService aprvdocService;

    @Autowired
    AprvlineService aprvlineService;

    @Autowired
    AuthService authService;

    @BeforeEach
    void signUp() {
        for (int i = 1; i <= 5; i++) {
            SignupDto.Request signupDto = new SignupDto.Request().builder()
                    .email("test@naver.com")
                    .password("1234")
                    .username("admin" + i)
                    .role("USER")
                    .build();

            boolean result = authService.signup(signupDto);
        }
    }

    @BeforeEach
    void addAprvdocDummy() {
        List<AprvlineDto.Request> aprvlineDtoList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                    .docNo("2022-11-11")
                    .seqNo((long) i)
                    .username("admin" + i)
                    .build();
            aprvlineDtoList.add(aprvlineDto);
        }

        AprvdocDto.Request aprvdocDto = new AprvdocDto.Request().builder()
                .docNo("2022-11-11")
                .title("홍길동 - 휴가신청서")
                .category("휴가신청서")
                .username("admin1")
                .aprvlineList(aprvlineDtoList)
                .aprvOrder("홍길동^김길동^박길동")
                .status(DocStatus.PROCEED.getCode())
                .build();

        aprvdocService.addAprvdoc(aprvdocDto);

        List<AprvlineDto.Request> aprvlineDtoList2 = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                    .docNo("2022-11-13")
                    .seqNo((long) i)
                    .username("admin" + i)
                    .build();
            aprvlineDtoList2.add(aprvlineDto);
        }

        AprvdocDto.Request aprvdocDto2 = new AprvdocDto.Request().builder()
                .docNo("2022-11-13")
                .title("홍길동 - 휴가신청서")
                .category("휴가신청서")
                .username("admin1")
                .aprvlineList(aprvlineDtoList2)
                .aprvOrder("홍길동^김길동^박길동")
                .status(DocStatus.ACCEPT.getCode())
                .build();

        aprvdocService.addAprvdoc(aprvdocDto2);
    }


    @Test
    @DisplayName("모든 결재문서조회")
    @Order(1)
    void findAll() {
        //given

        //when
        List<AprvdocDto.Response> result = aprvdocService.findAll();

        //then
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    @DisplayName("모든 결재문서 조회 - 이름조건")
    @Order(2)
    void findByusername() {
        //given
        AprvdocDto.Request request = AprvdocDto.Request.builder()
                .username("admin1")
                .build();

        //when
        List<AprvdocDto.Response> result = aprvdocService.findByUsername(request);

        //then
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    @DisplayName("결재할 문서 조회")
    @Order(3)
    void findInbox() {
        //given
        String username = "admin1";
        AprvdocDto.Request request = AprvdocDto.Request.builder()
                .username(username)
                .build();

        //when
        List<AprvdocDto.Response> result = aprvdocService.findInbox(request);
        AprvlineDto.Request request2 = AprvlineDto.Request.builder()
                .username(username)
                .docNo(result.get(0).getDocNo())
                .build();

        AprvlineDto.Response response = aprvlineService.findByDocNoAndAprvUser(request2);

        //then
        Assertions.assertEquals(AprvStatus.APRV_REQ.getCode(), response.getStatus());
    }

    @Test
    @DisplayName("결재 중 문서 조회")
    @Order(3)
    void findOutbox() {
        //given
        String username = "admin1";
        AprvdocDto.Request request = AprvdocDto.Request.builder()
                .username(username)
                .build();

        //when
        List<AprvdocDto.Response> result = aprvdocService.findOutbox(request);

        //then
        Assertions.assertEquals(DocStatus.PROCEED.getCode(), result.get(0).getStatus());
    }

    @Test
    @DisplayName("완료함 조회")
    @Order(4)
    void findArchive() {
        //given
        String username = "admin1";
        AprvdocDto.Request request = AprvdocDto.Request.builder()
                .username(username)
                .build();

        //when
        List<AprvdocDto.Response> result = aprvdocService.findArchive(request);

        //then
        Assertions.assertTrue(result.get(0).getStatus().equals(DocStatus.ACCEPT.getCode()) || result.get(0).getStatus().equals(DocStatus.REJECT.getCode()));
    }

    @Test
    @DisplayName("모든 결재문서 조회 - 이름/문서상태 조건")
    @Order(5)
    void findByusernameAndStatus() {
        //given
        AprvdocDto.Request request = AprvdocDto.Request.builder()
                .username("admin1")
                .status(DocStatus.ACCEPT.getCode())
                .build();

        //when
        List<AprvdocDto.Response> result = aprvdocService.findByUsernameAndStatus(request);

        //then
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    @DisplayName("결재문서저장")
    @Order(6)
    void addAprvdoc() {
        //given
        List<AprvlineDto.Request> aprvlineDtoList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                    .docNo("2022-11-12")
                    .seqNo((long) i)
                    .status(AprvStatus.APRV_ACCEPT.getCode())
                    .username("admin" + i)
                    .build();
            aprvlineDtoList.add(aprvlineDto);
        }

        AprvdocDto.Request aprvdocDto = new AprvdocDto.Request().builder()
                .docNo("2022-11-12")
                .title("홍길동 - 휴가신청서")
                .category("휴가신청서")
                .username("admin1")
                .aprvlineList(aprvlineDtoList)
                .aprvOrder("홍길동^김길동^박길동")
                .status(DocStatus.ACCEPT.getCode())
                .build();


        //when
        boolean result = aprvdocService.addAprvdoc(aprvdocDto);

        //then
        Assertions.assertEquals(true, result);
    }
}