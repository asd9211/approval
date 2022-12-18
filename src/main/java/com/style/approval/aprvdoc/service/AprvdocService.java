package com.style.approval.aprvdoc.service;

import com.style.approval.aprvdoc.dto.AprvdocDto;
import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.aprvdoc.repository.AprvdocRepository;
import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.aprvline.repository.AprvlineRepository;
import com.style.approval.aprvline.service.AprvlineService;
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
import java.util.Arrays;
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
    private final AprvlineService aprvlineService;
    private final AprvlineRepository aprvlineRepository;

    public List<AprvdocDto.Response> findAll() {
        List<AprvdocEntity> aprvdocList = aprvdocRepository.findAll();
        return parseResDtoList(aprvdocList);
    }

    public AprvdocDto.Response findByDocNo(AprvdocDto.Request request) {
        AprvdocEntity aprvdoc = aprvdocRepository.findByDocNo(request.getDocNo())
                .orElseThrow(() -> new ServiceException(ErrorCode.DOC_NOT_FOUND));

        List<AprvlineDto.Response> aprvlineDtoList = aprvlineRepository.findByDocNoOrderBySeqNoAsc(request.getDocNo())
                .stream()
                .map(aprvline -> new AprvlineDto.Response(aprvline))
                .collect(Collectors.toList());

        AprvdocDto.Response response = new AprvdocDto.Response(aprvdoc);
        response.setAprvlineList(aprvlineDtoList);

        return response;
    }

    public List<AprvdocDto.Response> findByUsername(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        List<AprvdocEntity> aprvdocList = aprvdocRepository.findByRegUserOrderByRegDateDesc(regUser);
        return parseResDtoList(aprvdocList);
    }

    public List<AprvdocDto.Response> findByUsernameAndStatus(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        List<AprvdocEntity> aprvdocList = aprvdocRepository.findByRegUserAndStatusOrderByRegDateDesc(regUser, request.getStatus());
        return parseResDtoList(aprvdocList);
    }

    public List<AprvdocDto.Response> findInbox(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        List<AprvdocEntity> aprvdocList = aprvdocRepository.findInboxJQPL(regUser.getId(), AprvStatus.APRV_REQ.getCode());
        return parseResDtoList(aprvdocList);
    }

    public List<AprvdocDto.Response> findOutbox(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        List<AprvdocEntity> aprvdocList = aprvdocRepository.findByRegUserAndStatusOrderByRegDateDesc(regUser, DocStatus.PROCEED.getCode());
        return parseResDtoList(aprvdocList);
    }

    public List<AprvdocDto.Response> findArchive(AprvdocDto.Request request) {
        UserEntity regUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        List<AprvdocEntity> aprvdocList = aprvdocRepository
                .findArchiveJQPL(regUser.getId(), Arrays.asList(DocStatus.ACCEPT.getCode(), DocStatus.REJECT.getCode()));

        return parseResDtoList(aprvdocList);
    }

    public boolean addAprvdoc(AprvdocDto.Request request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        request.setUser(user);

        AprvdocEntity aprvdoc = request.toEntity();

        aprvdocRepository.save(aprvdoc);

        Set<String> userDupChecker = new HashSet<>();

        for (int i = 0; i < request.getAprvlineList().size(); i++) {
            AprvlineDto.Request aprvlineDto = request.getAprvlineList().get(i);

            if (userDupChecker.contains(aprvlineDto.getUsername())) throw new ServiceException(ErrorCode.APRV_USER_DUP);
            userDupChecker.add(aprvlineDto.getUsername());

            aprvlineDto.setDocNo(request.getDocNo());
            aprvlineDto.setStatus(i == 0 ? AprvStatus.APRV_REQ.getCode() : AprvStatus.APRV_WAIT.getCode());

            aprvlineService.addAprvline(aprvlineDto);
        }

        return aprvdoc.getId() != null;
    }

    public List<AprvdocDto.Response> parseResDtoList(List<AprvdocEntity> aprvdocList) {
        return aprvdocList.stream()
                .map(aprvdoc -> new AprvdocDto.Response(aprvdoc))
                .collect(Collectors.toList());
    }

}
