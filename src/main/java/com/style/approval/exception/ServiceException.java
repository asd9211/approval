package com.style.approval.exception;

import com.style.approval.enums.ErrorCode;

public class ServiceException extends RuntimeException {
    private ErrorCode errorCode;

    public ServiceException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServiceException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
