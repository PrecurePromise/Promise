package com.pjt3.promise.controller;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pjt3.promise.response.MyPillHistoryGetRes;
import com.pjt3.promise.service.MyPillService;

@Api(tags={"마이필 리스트"})
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

		MyPillHistoryGetRes myPillHistoryGetRes =  myPillService.getMyPillHistoryList(authentication, pageNum);

		return ResponseEntity.status(200).body(myPillHistoryGetRes);

	}
}
