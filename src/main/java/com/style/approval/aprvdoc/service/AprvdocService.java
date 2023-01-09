package com.style.approval.aprvdoc.service;

import com.style.approval.aprvdoc.dto.AprvdocDto;
import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.aprvdoc.repository.AprvdocRepository;
import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.aprvline.entity.AprvlineEntity;
import com.style.approval.aprvline.repository.AprvlineRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AprvdocService {
    private final AprvdocRepository aprvdocRepository;
    private final UserRepository userRepository;
    private final AprvlineRepository aprvlineRepository;

    public List<AprvdocDto.Response> findAll() {
        return aprvdocRepository.findAll().stream().map(AprvdocDto.Response::new).collect(Collectors.toList());
    }

    public AprvdocDto.Response findByDocNo(AprvdocDto.Request request) {
        AprvdocEntity aprvdoc = aprvdocRepository.findByDocNo(request.getDocNo())
                .orElseThrow(() -> new ServiceException(ErrorCode.DOC_NOT_FOUND));
        return new AprvdocDto.Response(aprvdoc);
    }

    public List<AprvdocDto.Response> findByUsername(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        return aprvdocRepository.findByRegUserOrderByRegDateDesc(regUser).stream().map(AprvdocDto.Response::new).collect(Collectors.toList());
    }

    public List<AprvdocDto.Response> findByUsernameAndStatus(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        return aprvdocRepository.findByRegUserAndStatusOrderByRegDateDesc(regUser, request.getStatus()).stream().map(AprvdocDto.Response::new).collect(Collectors.toList());
    }

    public List<AprvdocDto.Response> findInbox(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        return aprvlineRepository.findByAprvUserAndStatus(regUser, AprvStatus.APRV_REQ.getCode())
                .stream()
                .map(row -> new AprvdocDto.Response(row.getAprvdoc()))
                .collect(Collectors.toList());
    }

    public List<AprvdocDto.Response> findOutbox(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        return aprvdocRepository.findByRegUserAndStatusOrderByRegDateDesc(regUser, DocStatus.PROCEED.getCode())
                .stream()
                .map(AprvdocDto.Response::new)
                .collect(Collectors.toList());
    }

    public List<AprvdocDto.Response> findArchive(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        return aprvlineRepository
                .findByAprvUser(regUser)
                .stream()
                .map(row -> new AprvdocDto.Response(row.getAprvdoc()))
                .filter(row -> row.getStatus().equals(DocStatus.ACCEPT.getCode()) || row.getStatus().equals(DocStatus.REJECT.getCode()))
                .collect(Collectors.toList());
    }

    public boolean addAprvdoc(AprvdocDto.Request request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        String docNo = request.getDocNo();

        AprvdocEntity aprvdoc = AprvdocEntity.builder()
                .docNo(docNo)
                .title(request.getTitle())
                .content(request.getContent())
                .regUser(user)
                .regDate(LocalDateTime.now())
                .status(request.getStatus())
                .category(request.getCategory())
                .aprvOrder(request.getAprvOrder())
                .build();

        aprvdocRepository.save(aprvdoc);

        Set<String> userDupChecker = new HashSet<>();

        for (int i = 0; i < request.getAprvlineList().size(); i++) {
            AprvlineDto.Request aprvlineDto = request.getAprvlineList().get(i);

            if (userDupChecker.contains(aprvlineDto.getUsername())) throw new ServiceException(ErrorCode.APRV_USER_DUP);
            userDupChecker.add(aprvlineDto.getUsername());

            aprvlineDto.setDocNo(request.getDocNo());
            aprvlineDto.setStatus(i == 0 ? AprvStatus.APRV_REQ: AprvStatus.APRV_WAIT);

            AprvlineEntity aprvline = AprvlineEntity.builder()
                    .docNo(aprvlineDto.getDocNo())
                    .seqNo(aprvlineDto.getSeqNo())
                    .status(aprvlineDto.getStatus())
                    .aprvdoc(aprvdoc)
                    .aprvUser(user)
                    .regDate(LocalDateTime.now())
                    .build();

            aprvlineRepository.save(aprvline);
        }

        return aprvdoc.getId() != null;
    }
}
