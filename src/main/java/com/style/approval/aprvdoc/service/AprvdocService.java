package com.style.approval.aprvdoc.service;

import com.style.approval.aprvdoc.dto.AprvdocDto;
import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.aprvdoc.repository.AprvdocRepository;
import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.aprvline.entity.AprvlineEntity;
import com.style.approval.enums.AprvStatus;
import com.style.approval.enums.DocStatus;
import com.style.approval.enums.ErrorCode;
import com.style.approval.exception.ServiceException;
import com.style.approval.user.entity.UserEntity;
import com.style.approval.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AprvdocService {
    private final AprvdocRepository aprvdocRepository;
    private final UserRepository userRepository;

    public List<AprvdocDto.Response> findAll() {
        return aprvdocRepository.findAll().stream().map(AprvdocDto.Response::new).collect(Collectors.toList());
    }

    public AprvdocDto.Response findByDocNo(AprvdocDto.Request request) {
        AprvdocEntity aprvdoc = aprvdocRepository.findByDocNo(request.getDocNo())
                .orElseThrow(() -> new ServiceException(ErrorCode.DOC_NOT_FOUND));
        return new AprvdocDto.Response(aprvdoc);
    }

    public List<AprvdocDto.Response> findByUsername(AprvdocDto.Request request) {
        UserEntity regUser = findUser(request.getUsername());

        return aprvdocRepository.findByRegUserOrderByRegDateDesc(regUser).stream().map(AprvdocDto.Response::new).collect(Collectors.toList());
    }

    public List<AprvdocDto.Response> findByUsernameAndStatus(AprvdocDto.Request request) {
        UserEntity regUser = findUser(request.getUsername());

        return aprvdocRepository.findByRegUserAndStatusOrderByRegDateDesc(regUser, request.getStatus()).stream().map(AprvdocDto.Response::new).collect(Collectors.toList());
    }

    public List<AprvdocDto.Response> findInbox(AprvdocDto.Request request) {
        UserEntity regUser = findUser(request.getUsername());

        return aprvdocRepository.findInbox(regUser, AprvStatus.APRV_REQ.getCode())
                .stream()
                .map(AprvdocDto.Response::new)
                .collect(Collectors.toList());
    }

    public List<AprvdocDto.Response> findOutbox(AprvdocDto.Request request) {
        UserEntity regUser = findUser(request.getUsername());

        return aprvdocRepository.findByRegUserAndStatusOrderByRegDateDesc(regUser, DocStatus.PROCEED.getCode())
                .stream()
                .map(AprvdocDto.Response::new)
                .collect(Collectors.toList());
    }

    public List<AprvdocDto.Response> findArchive(AprvdocDto.Request request) {
        UserEntity regUser = findUser(request.getUsername());

        return aprvdocRepository.findArchive(regUser, Arrays.asList(DocStatus.ACCEPT.getCode(), DocStatus.REJECT.getCode()))
                .stream()
                .map(AprvdocDto.Response::new)
                .collect(Collectors.toList());
    }

    public boolean addAprvdoc(AprvdocDto.Request request) {
        UserEntity user = findUser(request.getUsername());

        String docNo = request.getDocNo();

        AprvdocEntity aprvdoc = AprvdocEntity.builder()
                .docNo(docNo)
                .title(request.getTitle())
                .content(request.getContent())
                .regUser(user)
                .regDate(LocalDateTime.now())
                .status(request.getStatus())
                .category(request.getCategory())
                .aprvlines(new ArrayList<>())
                .aprvOrder(request.getAprvOrder())
                .build();

        aprvdocRepository.save(aprvdoc);

        parseAprvlines(request).forEach(
                row -> {
                    row.setAprvdoc(aprvdoc);
                    aprvdoc.getAprvlines().add(row);
                }
        );

        return true;
    }

    private List<AprvlineEntity> parseAprvlines(AprvdocDto.Request request) {
        Set<String> userDupChecker = new HashSet<>();
        List<AprvlineEntity> result = new ArrayList<>();

        for (int i = 0; i < request.getAprvlineList().size(); i++) {
            AprvlineDto.Request aprvlineDto = request.getAprvlineList().get(i);

            UserEntity aprvUser = userRepository.findByUsername(aprvlineDto.getUsername())
                    .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

            if (userDupChecker.contains(aprvlineDto.getUsername())) throw new ServiceException(ErrorCode.APRV_USER_DUP);
            userDupChecker.add(aprvlineDto.getUsername());

            aprvlineDto.setDocNo(request.getDocNo());
            aprvlineDto.setStatus(i == 0 ? AprvStatus.APRV_REQ : AprvStatus.APRV_WAIT);

            AprvlineEntity aprvline = AprvlineEntity.builder()
                    .docNo(aprvlineDto.getDocNo())
                    .seqNo(aprvlineDto.getSeqNo())
                    .status(aprvlineDto.getStatus().getCode())
                    .aprvUser(aprvUser)
                    .regDate(LocalDateTime.now())
                    .build();
            result.add(aprvline);
        }
        return result;
    }

    private UserEntity findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
    }
}
