package com.pjt3.promise.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* User Exception */
    DUPLICATED_EMAIL_NICKNAME(411, "UE001", "이메일과 닉네임 모두 사용중입니다.");

    private final int statusCode;
    private final String code;
    private final String message;

}
