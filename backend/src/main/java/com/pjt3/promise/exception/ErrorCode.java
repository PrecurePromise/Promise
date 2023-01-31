package com.pjt3.promise.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* User Exception */
    DUPLICATED_EMAIL_NICKNAME(411, "UE001", "이메일과 닉네임 모두 사용중입니다."),
    DUPLICATED_NICKNAME(410, "UE002", "다른 회원이 사용하고 계신 닉네임입니다."),
    DUPLICATED_NICKNAME_OWN(410, "UE007", "현재 회원님이 사용중인 닉네임입니다. (사용가능)"),
    DUPLICATED_EMAIL(409, "UE003", "이미 가입된 이메일입니다."),

    CANNOT_DELETE_USER(404, "UE004", "회원 탈퇴중에 문제가 발생하였습니다."),
    CANNOT_UPDATE_USER(404, "UE005", "회원 정보 수정 중에 문제가 발생하였습니다."),
    CANNOT_UPDATE_PROFILE(404, "UE006", "프로필 사진 업데이트 중에 문제가 발생했습니다."),

    /* Auth Exception */
    EXPIRED_AUTH_TOKEN(420, "AE001", "만료된 토큰입니다."),

    ;


    private final int statusCode;
    private final String code;
    private final String message;

}
