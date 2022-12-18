package com.style.approval.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_DUP(400, "A001", "동일 ID의 회원이 존재합니다."),
    USER_NOT_FOUND(400, "A002", "ID와 일치하는 계정이 없습니다."),
    DOC_NOT_FOUND(400, "B001", "문서번호와 일치하는 결재문서가 없습니다."),
    APRV_NOT_FOUND(400, "C001", "일치하는 결재라인이 없습니다"),
    APRV_ORDER_INCORRECT(400, "C002", "일치하는 결재라인이 없습니다"),
    APRV_USER_DUP(400, "C002", "결재자가 중복됩니다."),
    RESOURCE_NOT_FOUND(400, "C003", "본인 결재순서가 아닙니다."),
    APRV_CODE_NOT_FOUND(400, "C004", "일치하는 결재상태코드가 없습니다."),
    APRV_DOCNO_INCORRECT(400, "C004", "문서번호와 결재라인의 문서번호가 일치하지 않습니다.");


    private int status;
    private String code;
    private String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getKey() {
        return this.code;
    }

    public String getValue() {
        return this.message;
    }
}
