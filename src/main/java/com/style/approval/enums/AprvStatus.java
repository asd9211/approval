package com.style.approval.enums;

import com.style.approval.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AprvStatus {
    APRV_REQ("1"),
    APRV_WAIT("2"),
    APRV_ACCEPT("3"),
    APRV_REJECT("4");

    private String code;

    public static AprvStatus findByAprvStatusCode(String code){
        return Arrays.stream(AprvStatus.values())
                .filter(aprvStatus -> aprvStatus.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ServiceException(ErrorCode.APRV_CODE_NOT_FOUND));
    }

}
