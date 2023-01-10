package com.style.approval.aprvdoc.service;

import com.style.approval.aprvdoc.dto.AprvdocDto;
import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.aprvdoc.repository.AprvdocRepository;
import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.aprvline.entity.AprvlineEntity;
import com.style.approval.aprvline.repository.AprvlineRepository;
import com.style.approval.auth.dto.SignupDto;
import com.style.approval.enums.AprvStatus;
import com.style.approval.enums.DocStatus;
import com.style.approval.user.entity.UserEntity;
import com.style.approval.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AprvdocServiceTest {

    @InjectMocks
    private AprvdocService aprvdocService;

    @Mock
    private AprvdocRepository aprvdocRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("모든 결재문서조회")
    @Order(1)
    void findAll() {
        //given
        String username = "admin1";
        doReturn(Arrays.asList(aprvdoc(username, DocStatus.ACCEPT))).when(aprvdocRepository)
                .findAll();

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
        String username = "admin1";
        AprvdocDto.Request request = AprvdocDto.Request.builder()
                .username(username)
                .build();

        doReturn(Optional.of(user(username))).when(userRepository)
                .findByUsername(any(String.class));

        doReturn(Arrays.asList(aprvdoc(username, DocStatus.PROCEED))).when(aprvdocRepository)
                .findByRegUserOrderByRegDateDesc(any(UserEntity.class));

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

        doReturn(Optional.of(user(username))).when(userRepository)
                .findByUsername(any(String.class));

        doReturn(Arrays.asList(aprvdoc(username, DocStatus.PROCEED))).when(aprvdocRepository)
                .findInbox(any(UserEntity.class), any(String.class));

        //when
        List<AprvdocDto.Response> result = aprvdocService.findInbox(request);

        //then
        Assertions.assertEquals(AprvStatus.APRV_REQ.getCode(), result.get(0).getAprvlineList().get(0).getStatus());
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


        doReturn(Optional.of(user(username))).when(userRepository)
                .findByUsername(any(String.class));

        doReturn(Arrays.asList(aprvdoc(username, DocStatus.PROCEED))).when(aprvdocRepository)
                .findByRegUserAndStatusOrderByRegDateDesc(any(UserEntity.class), any(String.class));

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

        doReturn(Optional.of(user(username))).when(userRepository)
                .findByUsername(any(String.class));

        doReturn(Arrays.asList(aprvdoc(username, DocStatus.ACCEPT))).when(aprvdocRepository)
                .findArchive(any(UserEntity.class), any(List.class));

        //when
        List<AprvdocDto.Response> result = aprvdocService.findArchive(request);

        //then
        String status = result.get(0).getStatus();
        Assertions.assertTrue(status.equals(DocStatus.ACCEPT.getCode()) || status.equals(DocStatus.REJECT.getCode()));
    }

    @Test
    @DisplayName("모든 결재문서 조회 - 이름/문서상태 조건")
    @Order(5)
    void findByusernameAndStatus() {
        //given
        String username = "admin1";
        AprvdocDto.Request request = AprvdocDto.Request.builder()
                .username(username)
                .status(DocStatus.ACCEPT.getCode())
                .build();

        doReturn(Optional.of(user(username))).when(userRepository)
                .findByUsername(any(String.class));

        doReturn(Arrays.asList(aprvdoc(username, DocStatus.ACCEPT))).when(aprvdocRepository)
                .findByRegUserAndStatusOrderByRegDateDesc(any(UserEntity.class), any(String.class));

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
        String username = "admin1";
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo("2022-11-12")
                .username(username)
                .build();

        AprvdocDto.Request aprvdocDto = new AprvdocDto.Request().builder()
                .docNo("2022-11-12")
                .title("홍길동 - 휴가신청서")
                .category("휴가신청서")
                .username(username)
                .aprvlineList(Arrays.asList(aprvlineDto))
                .aprvOrder("홍길동^김길동^박길동")
                .status(DocStatus.PROCEED.getCode())
                .build();

        doReturn(Optional.of(user(username))).when(userRepository)
                .findByUsername(any(String.class));

        doReturn(toEntity(aprvdocDto)).when(aprvdocRepository)
                .save(any(AprvdocEntity.class));

        //when
        boolean result = aprvdocService.addAprvdoc(aprvdocDto);

        //then
        Assertions.assertEquals(true, result);
    }


    UserEntity user(int i) {
        SignupDto.Request signupDto = new SignupDto.Request().builder()
                .email("test@naver.com")
                .password("1234")
                .username("admin" + i)
                .role("USER")
                .build();
        return new UserEntity(signupDto);
    }

    UserEntity user(String user) {
        SignupDto.Request signupDto = new SignupDto.Request().builder()
                .email("test@naver.com")
                .password("1234")
                .username(user)
                .role("USER")
                .build();
        return new UserEntity(signupDto);
    }

    AprvdocEntity aprvdoc(String name, DocStatus docStatus) {
        List<AprvlineEntity> aprvlineList = new ArrayList<>();

        AprvdocEntity aprvdoc = AprvdocEntity.builder()
                .docNo("2022-11-11")
                .title("홍길동 - 휴가신청서")
                .category("휴가신청서")
                .regUser(user(name))
                .aprvOrder("홍길동^김길동^박길동")
                .aprvlines(aprvlineList)
                .status(docStatus.getCode())
                .build();

        for (int i = 1; i <= 5; i++) {
            UserEntity user = user(i);
            AprvlineEntity aprvline = AprvlineEntity.builder()
                    .docNo("2022-11-11")
                    .seqNo((long) i)
                    .aprvUser(user)
                    .aprvdoc(aprvdoc)
                    .status(user.getUsername().equals(name) ? AprvStatus.APRV_REQ.getCode() : AprvStatus.APRV_WAIT.getCode())
                    .build();
            aprvlineList.add(aprvline);
        }

        return aprvdoc;
    }

    AprvdocEntity toEntity(AprvdocDto.Request request) {
        return AprvdocEntity.builder()
                .docNo(request.getDocNo())
                .title(request.getTitle())
                .category(request.getCategory())
                .regUser(user(request.getUsername()))
                .aprvOrder(request.getAprvOrder())
                .status(request.getStatus())
                .build();
    }

}