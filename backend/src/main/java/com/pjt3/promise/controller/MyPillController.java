package com.pjt3.promise.controller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pjt3.promise.common.auth.PMUserDetails;
import com.pjt3.promise.common.response.BaseResponseBody;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.response.MyPillHistoryGetRes;
import com.pjt3.promise.service.MyPillService;

@RequestMapping("/mypills")
@RestController
@RequiredArgsConstructor
public class MyPillController {

    private final MyPillService myPillService;
    
	@GetMapping()
	public ResponseEntity<?> getMyPillList(Authentication authentication) {

		Map<String, List> alarmMap = myPillService.getMyPillMap(authentication);

		return ResponseEntity.status(200).body(alarmMap);

	}
	
	@GetMapping("/history")
	public ResponseEntity<?> getMyPillHistoryList(Authentication authentication, @RequestParam int pageNum) {
		MyPillHistoryGetRes myPillHistoryGetRes = new MyPillHistoryGetRes();
		try {

			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();

			myPillHistoryGetRes = myPillService.getMyPillHistoryList(user, pageNum);

			return ResponseEntity.status(200).body(myPillHistoryGetRes);

		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
		}

	}
}
