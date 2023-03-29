package com.pjt3.promise.service;

import java.util.List;
import java.util.Map;

import com.pjt3.promise.entity.User;
import com.pjt3.promise.response.MyAlarmHistory;
import com.pjt3.promise.response.MyPillGetRes;
import com.pjt3.promise.response.MyPillHistoryGetRes;
import org.springframework.security.core.Authentication;

public interface MyPillService {

	Map<String, List> getMyPillMap(Authentication authentication);

	MyPillHistoryGetRes getMyPillHistoryList(User user, int pageNum);

}
