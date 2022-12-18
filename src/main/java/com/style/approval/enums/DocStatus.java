package com.style.approval.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DocStatus {
    TEMP_SAVE("1"),
    PROCEED("2"),
    ACCEPT("3"),
    REJECT("4");

    private String code;
}
