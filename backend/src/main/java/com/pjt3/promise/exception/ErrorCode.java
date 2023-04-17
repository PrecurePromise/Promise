package com.pjt3.promise.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* User Exception */
    DUPLICATED_EMAIL_NICKNAME(411, "UE001", "이메일과 닉네임 모두 사용중입니다."),
    DUPLICATED_NICKNAME(410, "UE002", "다른 회원이 사용하고 계신 닉네임입니다."),
    DUPLICATED_NICKNAME_OWN(410, "UE003", "현재 회원님이 사용중인 닉네임입니다. (사용가능)"),
    DUPLICATED_EMAIL(409, "UE004", "이미 가입된 이메일입니다."),

    CANNOT_DELETE_USER(404, "UE005", "회원 탈퇴중에 문제가 발생하였습니다."),
    CANNOT_UPDATE_USER(404, "UE006", "회원 정보 수정 중에 문제가 발생하였습니다."),
    CANNOT_UPDATE_PROFILE(404, "UE007", "프로필 사진 업데이트 중에 문제가 발생했습니다."),
    CANNOT_FOUND_USER(404, "UE008", "검색하신 키워드에 맞는 회원님이 없습니다"),

    /* Auth Exception */
    EXPIRED_AUTH_TOKEN(420, "AE001", "만료된 토큰입니다."),

    /* Alarm Exception */
    CANNOT_INSERT_ALARM(500, "MAE001", "알람 등록에 실패했습니다."),
    CANNOT_INSERT_ALARM_MEDI(500, "MAE002", "알람 약 등록에 실패했습니다."),
    CANNOT_INSERT_ALARM_TAG(500, "MAE003", "알람 태그 등록에 실패했습니다."),
    CANNOT_UPDATE_ALARM(500, "MAE004", "알람 수정에 실패했습니다."),
    CANNOT_UPDATE_ALARM_MEDI(500, "MAE005", "알람 약 수정에 실패했습니다."),
    CANNOT_UPDATE_ALARM_TAG(500, "MAE006", "알람 태그 수정에 실패했습니다."),
    CANNOT_FIND_ALARM(404, "MAE007", "알람 정보를 찾을 수 없습니다."),
    CANNOT_DELETE_ALARM(500, "MAE008", "알람 삭제 중에 문제가 발생했습니다."),
    CANNOT_INSERT_ALARM_TAKE_HISTORY(500, "MAE009", "알람 이력 등록 중에 문제가 발생했습니다."),
    NO_INPUT_OCR_TEXT(500, "MAE010", "OCR - 인식된 문자열이 없습니다."),

    /* AlarmShare Exception */
    CANNOT_INSERT_ALARM_SHARE(500, "ASE001", "알람 공유 등록에 실패했습니다."),
    CANNOT_INSERT_ALARM_SHARE_MEDI(500, "ASE002", "알람 공유 약 등록에 실패했습니다."),

    /* Pet Exception */
    CANNOT_UPDATE_PET_EXP(500, "PE001", "펫 경험치 등록 실패");
    ;


    private final int statusCode;
    private final String code;
    private final String message;

}
