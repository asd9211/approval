package com.style.approval.aprvline.service;

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
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AprvlineService {

    private final AprvdocRepository aprvdocRepository;
    private final AprvlineRepository aprvlineRepository;
    private final UserRepository userRepository;

    public List<AprvlineDto.Response> findAllByDocNo(AprvlineDto.Request request) {
        return aprvlineRepository.findByDocNoOrderBySeqNoAsc(request.getDocNo()).stream()
                .map(entity -> new AprvlineDto.Response(entity))
                .collect(Collectors.toList());
    }

    public AprvlineDto.Response findByDocNoAndAprvUser(AprvlineDto.Request request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        return aprvlineRepository.findByDocNoAndAprvUser(request.getDocNo(), user).map(entity -> new AprvlineDto.Response(entity)).orElse(null);
    }

    public boolean addAprvline(AprvlineDto.Request request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        AprvdocEntity aprvdoc = aprvdocRepository.findByDocNo(request.getDocNo())
                .orElseThrow(() -> new ServiceException(ErrorCode.DOC_NOT_FOUND));

        request.setUser(user);
        request.setAprvdoc(aprvdoc);
        AprvlineEntity aprvline = request.toEntity();

        aprvlineRepository.save(aprvline);

        return aprvline.getId() != null;
    }

    public boolean modifyAprvline(AprvlineDto.Request request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        AprvlineEntity aprvline = aprvlineRepository.findByDocNoAndAprvUser(request.getDocNo(), user)
                .orElseThrow(() -> new ServiceException(ErrorCode.APRV_NOT_FOUND));

        aprvline.setStatus(request.getStatus());
        aprvline.setComment(request.getComment());
        aprvline.setAprvDate(LocalDateTime.now());

        return aprvline.getId() != null;
    }

    public boolean acceptAprvline(AprvlineDto.Request request) {
        if (isMyTurn(request)) {
            String acceptCode = AprvStatus.APRV_ACCEPT.getCode();

            if (!userRepository.existsByUsername(request.getUsername()))
                throw new ServiceException(ErrorCode.USER_NOT_FOUND);

            AprvlineEntity aprvline = aprvlineRepository.findByDocNoAndSeqNo(request.getDocNo(), request.getSeqNo())
                    .orElseThrow(() -> new ServiceException(ErrorCode.APRV_NOT_FOUND));

            request.setStatus(acceptCode);
            aprvline.setStatus(acceptCode);
            aprvline.setComment(request.getComment());
            aprvline.setAprvDate(LocalDateTime.now());

            reqNextAprvline(request);

            return aprvline.getId() != null;
        }

        throw new ServiceException(ErrorCode.APRV_ORDER_INCORRECT);
    }

    public boolean rejectAprvline(AprvlineDto.Request request) {
        if (isMyTurn(request)) {
            String rejectCode = AprvStatus.APRV_REJECT.getCode();

            if (!userRepository.existsByUsername(request.getUsername()))
                throw new ServiceException(ErrorCode.USER_NOT_FOUND);

            AprvlineEntity aprvline = aprvlineRepository.findByDocNoAndSeqNo(request.getDocNo(), request.getSeqNo())
                    .orElseThrow(() -> new ServiceException(ErrorCode.APRV_NOT_FOUND));

            request.setStatus(rejectCode);
            aprvline.setStatus(rejectCode);
            aprvline.setComment(request.getComment());
            aprvline.setAprvDate(LocalDateTime.now());

            completeAprvdoc(request);

            return aprvline.getId() != null;
        }

        throw new ServiceException(ErrorCode.APRV_ORDER_INCORRECT);
    }

    public boolean isMyTurn(AprvlineDto.Request request) {
        List<AprvlineEntity> aprvlineList = aprvlineRepository.findByDocNoOrderBySeqNoAsc(request.getDocNo());
        for (AprvlineEntity aprvline : aprvlineList) {
            boolean aprvUserCorrect = request.getUsername().equals(aprvline.getAprvUser().getUsername());
            if (aprvUserCorrect && aprvline.getStatus().equals(AprvStatus.APRV_REQ.getCode())) {
                return true;
            }
        }
        return false;
    }

    public void reqNextAprvline(AprvlineDto.Request request) {
        AprvlineEntity nextAprvline = aprvlineRepository.findByDocNoAndSeqNo(request.getDocNo(), request.getSeqNo() + 1).orElse(null);

        // 다음 결재자가 없을 경우 문서 완료
        if (ObjectUtils.isEmpty(nextAprvline)) {
            completeAprvdoc(request);
            // 다음 결재자가 있을 경우 다음 결재자 상태 update(대기 -> 요청)
        } else {
            nextAprvline.setStatus(AprvStatus.APRV_REQ.getCode());
            aprvlineRepository.save(nextAprvline);
        }
    }

    public void completeAprvdoc(AprvlineDto.Request request) {
        AprvdocEntity aprvdoc = aprvdocRepository.findByDocNo(request.getDocNo())
                .orElseThrow(() -> new ServiceException(ErrorCode.APRV_NOT_FOUND));
        aprvdoc.setEndDate(LocalDateTime.now());
        aprvdoc.setStatus(request.getStatus().equals(AprvStatus.APRV_ACCEPT.getCode()) ? DocStatus.ACCEPT.getCode() : DocStatus.REJECT.getCode());
        aprvdocRepository.save(aprvdoc);
    }
}