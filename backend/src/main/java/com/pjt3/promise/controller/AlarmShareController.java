package com.pjt3.promise.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pjt3.promise.common.auth.PMUserDetails;
import com.pjt3.promise.common.response.BaseResponseBody;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.request.AlarmShareAcceptReq;
import com.pjt3.promise.response.AlarmShareGetRes;
import com.pjt3.promise.service.AlarmShareService;

@RequestMapping("/sharings")
@RestController
@RequiredArgsConstructor
public class AlarmShareController {

    private final AlarmShareService alarmShareService;

	@GetMapping()
	public ResponseEntity<?> getAlarmShareList(Authentication authentication){

		Map<String, List> alarmShareMap = alarmShareService.getAlarmShareMap(authentication);

		return ResponseEntity.status(200).body(alarmShareMap);
//		try {
//
//			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
//			User user = userDetails.getUser();
//
//	        List<AlarmShareGetRes> alarmShareList = alarmShareService.getAlarmShareList(user);
//
//	        Map<String, List> map = new HashMap<String, List>();
//			map.put("alarmShareList", alarmShareList);
//
//			return ResponseEntity.status(200).body(map);
//
//		} catch (NullPointerException e) {
//			return ResponseEntity.status(400).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
//		} catch (Exception e) {
//			return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
//		}

	}
	
	@PostMapping("/accept")
	public ResponseEntity<?> acceptAlarmShare(Authentication authentication, @RequestBody AlarmShareAcceptReq alarmShareAcceptReq){
		try {	
			
			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();
	        
			int result = 0;
	        result = alarmShareService.acceptAlarmShare(user, alarmShareAcceptReq);
			
	        if(result == 1) {			
				return ResponseEntity.status(200).body(BaseResponseBody.of(200, "알람 수락 성공"));
			} else {
				return ResponseEntity.status(500).body(BaseResponseBody.of(500, "알람 수락 실패"));
			}
			
		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
		}

	}
	
	@DeleteMapping("/reject")
	public ResponseEntity<?> rejectAlarmShare(Authentication authentication, @RequestParam int asId){
		try {	
	        
			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();
			
			int result = 0;
	        result = alarmShareService.rejectAlarmShare(asId);
			
	        if(result == 1) {			
				return ResponseEntity.status(200).body(BaseResponseBody.of(200, "알람 거절 성공"));
			} else {
				return ResponseEntity.status(500).body(BaseResponseBody.of(500, "알람 거절 실패"));
			}
			
		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
		}

	}
	
}
