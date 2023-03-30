package com.pjt3.promise.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pjt3.promise.common.auth.PMUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.pjt3.promise.entity.User;
import com.pjt3.promise.repository.MediAlarmRepositorySupport;
import com.pjt3.promise.response.MyAlarmHistory;
import com.pjt3.promise.response.MyPillGetRes;
import com.pjt3.promise.response.MyPillHistoryGetRes;

@Service
@RequiredArgsConstructor
public class MyPillServiceImpl implements MyPillService {

	private final MediAlarmRepositorySupport mediAlarmRepositorySupport;

	@Override
	public Map<String, List> getMyPillMap(Authentication authentication) {

		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
		User user = userDetails.getUser();

		LocalDate now = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String today = now.format(formatter);

		List<MyPillGetRes> alarmList = mediAlarmRepositorySupport.getMyPillList(user, today);
		
		Map<String, List> map = new HashMap<>();
		map.put("alarmList", alarmList);

		return map;
	}
	
    private int calcTotalPageCnt(int total, int limit) {
        int totalPageCnt = 0;
        if (total % limit > 0) totalPageCnt = total / limit + 1;
        else totalPageCnt = total / limit;
        return totalPageCnt;
    }


	@Override
	public MyPillHistoryGetRes getMyPillHistoryList(Authentication authentication, int pageNum) {
		
		MyPillHistoryGetRes myPillHistoryGetRes = new MyPillHistoryGetRes();

		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
		User user = userDetails.getUser();

		int limit = 5;

		int total = mediAlarmRepositorySupport.getTotalCountMyPillHistoryList(user);
		int totalPageCnt = calcTotalPageCnt(total, limit);
		int offset = (pageNum-1)*limit;
		List<MyAlarmHistory> alarmHistoryList = mediAlarmRepositorySupport.getMyPillHistoryList(user, limit, offset);
		
		myPillHistoryGetRes.setTotalPageCnt(totalPageCnt);
		myPillHistoryGetRes.setAlarmHistoryList(alarmHistoryList);

		return myPillHistoryGetRes;

	}

}
