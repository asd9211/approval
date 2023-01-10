package com.style.approval.aprvline.service;

import com.style.approval.aprvdoc.dto.AprvdocDto;
import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.aprvdoc.repository.AprvdocRepository;
import com.style.approval.aprvdoc.service.AprvdocService;
import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.aprvline.entity.AprvlineEntity;
import com.style.approval.aprvline.repository.AprvlineRepository;
import com.style.approval.auth.dto.SignupDto;
import com.style.approval.auth.service.AuthService;
import com.style.approval.enums.AprvStatus;
import com.style.approval.enums.DocStatus;
import com.style.approval.user.entity.UserEntity;
import com.style.approval.user.repository.UserRepository;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AprvlineServiceTest {

    @InjectMocks
    private AprvlineService aprvlineService;
    @Mock
    private AprvdocRepository aprvdocRepository;
    @Mock
    private AprvlineRepository aprvlineRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("결재라인조회 - 문서번호")
    @Order(1)
    void findAprvlineByDocNo() {
        //given
        String docNo = "2022-11-11";
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo(docNo)
                .build();

        doReturn(aprvlines(docNo, AprvStatus.APRV_WAIT)).when(aprvlineRepository)
                .findByDocNoOrderBySeqNoAsc(any(String.class));

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

        doReturn(Optional.of(user(aprvUser))).when(userRepository)
                .findByUsername(any(String.class));

        doReturn(Optional.of(aprvlines(docNo, aprvUser, AprvStatus.APRV_WAIT))).when(aprvlineRepository)
                .findByDocNoAndAprvUser(any(String.class), any(UserEntity.class));

        //when
        AprvlineDto.Response result = aprvlineService.findByDocNoAndAprvUser(aprvlineDto);

        //then
        Assertions.assertEquals(docNo, result.getDocNo());
        Assertions.assertEquals(aprvUser, result.getUsername());
    }

    @Test
    @DisplayName("결재승인")
    @Order(3)
    void acceptAprvline() {
        //given
        String aprvUser = "tester";
        String docNo = "2022-11-11";
        Long seqNo = 1L;
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo(docNo)
                .seqNo(seqNo)
                .username(aprvUser)
                .build();

        doReturn(Optional.of(user(aprvUser))).when(userRepository)
                .findByUsername(any(String.class));

        doReturn(Optional.of(aprvlines(docNo, aprvUser, AprvStatus.APRV_REQ))).when(aprvlineRepository)
                .findByDocNoAndSeqNo(any(String.class), any(Long.class));

        doReturn(Arrays.asList(aprvlines(docNo, aprvUser, AprvStatus.APRV_REQ))).when(aprvlineRepository)
                .findByDocNoOrderBySeqNoAsc(any(String.class));

        //when
        boolean result = aprvlineService.acceptAprvline(aprvlineDto);

        //then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("결재기각")
    @Order(4)
    void rejectAprvline() {
        //given
        String aprvUser = "tester";
        String docNo = "2022-11-11";
        Long seqNo = 1L;
        AprvlineDto.Request aprvlineDto = new AprvlineDto.Request().builder()
                .docNo(docNo)
                .seqNo(seqNo)
                .username(aprvUser)
                .build();

        doReturn(Optional.of(user(aprvUser))).when(userRepository)
                .findByUsername(any(String.class));

        doReturn(Optional.of(aprvlines(docNo, aprvUser, AprvStatus.APRV_REQ))).when(aprvlineRepository)
                .findByDocNoAndSeqNo(any(String.class), any(Long.class));

        doReturn(Optional.of(aprvdoc(docNo, aprvUser, DocStatus.PROCEED))).when(aprvdocRepository)
                .findByDocNo(any(String.class));

        doReturn(Arrays.asList(aprvlines(docNo, aprvUser, AprvStatus.APRV_REQ))).when(aprvlineRepository)
                .findByDocNoOrderBySeqNoAsc(any(String.class));

        //when
        boolean result = aprvlineService.rejectAprvline(aprvlineDto);

        //then
        Assertions.assertTrue(result);
    }


    AprvdocEntity aprvdoc(String docNo, String name, DocStatus docStatus) {
        return AprvdocEntity.builder()
                .docNo("2022-11-11")
                .title("홍길동 - 휴가신청서")
                .category("휴가신청서")
                .regUser(user(name))
                .aprvOrder("홍길동^김길동^박길동")
                .aprvlines(new ArrayList<>())
                .status(docStatus.getCode())
                .build();
    }

    List<AprvlineEntity> aprvlines(String docNo, AprvStatus status) {
        List<AprvlineEntity> aprvlineList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            UserEntity user = user(i);
            AprvlineEntity aprvline = AprvlineEntity.builder()
                    .docNo(docNo)
                    .seqNo((long) i)
                    .aprvUser(user)
                    .status(status.getCode())
                    .build();
            aprvlineList.add(aprvline);
        }

        return aprvlineList;
    }

    AprvlineEntity aprvlines(String docNo, String username, AprvStatus status) {
        return AprvlineEntity.builder()
                .id(1L)
                .docNo(docNo)
                .seqNo(1L)
                .aprvUser(user(username))
                .status(status.getCode())
                .build();
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

}