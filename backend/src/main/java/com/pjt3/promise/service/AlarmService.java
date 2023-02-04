package com.pjt3.promise.service;

import java.util.List;

import com.pjt3.promise.entity.User;
import com.pjt3.promise.request.AlarmPostReq;
import com.pjt3.promise.request.AlarmPutReq;
import com.pjt3.promise.request.TakeHistoryPostReq;
import com.pjt3.promise.response.AlarmCalendarGetRes;
import com.pjt3.promise.response.AlarmDetailGetRes;
import com.pjt3.promise.response.AlarmGetRes;
import com.pjt3.promise.response.AlarmHistoryGetRes;
import com.pjt3.promise.response.AlarmMainGetRes;
import com.pjt3.promise.response.AlarmOCRRes;
import org.springframework.security.core.Authentication;

public interface AlarmService {
    int insertAlarm(Authentication authentication, AlarmPostReq alarmsPostReq);

	void updateAlarm(Authentication authentication, AlarmPutReq alarmPutReq);

	void deleteAlarm(int alarmId);

	AlarmDetailGetRes getAlarmInfo(int alarmId);

	int insertTakeHistory(User user, TakeHistoryPostReq takeHistoryPostReq);

	List<AlarmGetRes> getDateAlarmList(User user, String nowDate);

	AlarmHistoryGetRes getPastAlarmList(int periodType, User user);

	List<AlarmOCRRes> getOCRMediList(String text);

	List<AlarmCalendarGetRes> getMonthAlarmList(User user, String nowMonth);

	AlarmMainGetRes getMainAlarmList(User user);
}
