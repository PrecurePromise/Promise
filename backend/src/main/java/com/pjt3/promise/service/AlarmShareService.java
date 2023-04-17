package com.pjt3.promise.service;

import java.util.List;
import java.util.Map;

import com.pjt3.promise.entity.User;
import com.pjt3.promise.request.AlarmShareAcceptReq;
import com.pjt3.promise.response.AlarmShareGetRes;
import org.springframework.security.core.Authentication;

public interface AlarmShareService {

	Map<String, List> getAlarmShareMap(Authentication authentication);

	int acceptAlarmShare(Authentication authentication, AlarmShareAcceptReq alarmShareAcceptReq);

	int rejectAlarmShare(int asId);

}
