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
import java.util.ArrayList;
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
        return aprvlineRepository.findByDocNoOrderBySeqNoAsc(request.getDocNo())
                .stream()
                .map(AprvlineDto.Response::new).collect(Collectors.toList());
    }

    public AprvlineDto.Response findByDocNoAndAprvUser(AprvlineDto.Request request) {
        UserEntity user = findUser(request.getUsername());

        AprvlineEntity aprvline = aprvlineRepository.findByDocNoAndAprvUser(request.getDocNo(), user).orElse(null);

        return ObjectUtils.isEmpty(aprvline) ? null : new AprvlineDto.Response(aprvline);
    }


    public boolean acceptAprvline(AprvlineDto.Request request) {
        if (isMyTurn(request)) {
            findUser(request.getUsername());

            AprvlineEntity aprvline = aprvlineRepository.findByDocNoAndSeqNo(request.getDocNo(), request.getSeqNo())
                    .orElseThrow(() -> new ServiceException(ErrorCode.APRV_NOT_FOUND));

            aprvline.setStatus(AprvStatus.APRV_ACCEPT);
            aprvline.setComment(request.getComment());
            aprvline.setAprvDate(LocalDateTime.now());

            reqNextAprvline(request);

            return aprvline.getId() != null;
        }

        throw new ServiceException(ErrorCode.APRV_ORDER_INCORRECT);
    }

    public boolean rejectAprvline(AprvlineDto.Request request) {
        if (isMyTurn(request)) {
            findUser(request.getUsername());

            AprvlineEntity aprvline = aprvlineRepository.findByDocNoAndSeqNo(request.getDocNo(), request.getSeqNo())
                    .orElseThrow(() -> new ServiceException(ErrorCode.APRV_NOT_FOUND));

            aprvline.setStatus(AprvStatus.APRV_REJECT);
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

            if (aprvUserCorrect && aprvline.getStatus() == AprvStatus.APRV_REQ) {
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
            nextAprvline.setStatus(AprvStatus.APRV_REQ);
        }
    }

    public void completeAprvdoc(AprvlineDto.Request request) {
        AprvdocEntity aprvdoc = aprvdocRepository.findByDocNo(request.getDocNo())
                .orElseThrow(() -> new ServiceException(ErrorCode.APRV_NOT_FOUND));
        aprvdoc.setEndDate(LocalDateTime.now());
        aprvdoc.setStatus(request.getStatus() == AprvStatus.APRV_ACCEPT ? DocStatus.ACCEPT : DocStatus.REJECT);
    }

    private UserEntity findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
    }
}
