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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pjt3.promise.common.auth.PMUserDetails;
import com.pjt3.promise.common.response.BaseResponseBody;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.request.AlarmOCRPostReq;
import com.pjt3.promise.request.AlarmPostReq;
import com.pjt3.promise.request.AlarmPutReq;
import com.pjt3.promise.request.TakeHistoryPostReq;
import com.pjt3.promise.response.AlarmCalendarGetRes;
import com.pjt3.promise.response.AlarmDetailGetRes;
import com.pjt3.promise.response.AlarmGetRes;
import com.pjt3.promise.response.AlarmHistoryGetRes;
import com.pjt3.promise.response.AlarmMainGetRes;
import com.pjt3.promise.response.AlarmOCRRes;
import com.pjt3.promise.service.AlarmService;
import com.pjt3.promise.service.PetService;

@RequestMapping("/alarms")
@RestController
@RequiredArgsConstructor
public class AlarmController {

	private final AlarmService alarmService;
	private final PetService petService;

	@PostMapping()
	public ResponseEntity<?> insertAlarm(Authentication authentication, @RequestBody AlarmPostReq alarmPostReq) {

		int result = alarmService.insertAlarm(authentication, alarmPostReq);

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("alarmId", result);

		return ResponseEntity.status(200).body(map);

	}

	@PutMapping()
	public ResponseEntity<?> updateAlarm(Authentication authentication, @RequestBody AlarmPutReq alarmPutReq) {

		alarmService.updateAlarm(authentication, alarmPutReq);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "알람 수정 성공"));
	}

	@DeleteMapping("/{alarmId}")
	public ResponseEntity<?> deleteAlarm(Authentication authentication, @PathVariable int alarmId) {

		alarmService.deleteAlarm(alarmId);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "알람 삭제 성공"));

	}

	@GetMapping("/detail/{alarmId}")
	public ResponseEntity<?> getAlarmInfo(Authentication authentication, @PathVariable int alarmId) {

		AlarmDetailGetRes alarmDetailGetRes = alarmService.getAlarmInfo(alarmId);

		return ResponseEntity.status(200).body(alarmDetailGetRes);
	}

	@PostMapping("/check")
	public ResponseEntity<?> insertTakeHistory(Authentication authentication,
			@RequestBody TakeHistoryPostReq takeHistoryPostReq) {

		alarmService.insertTakeHistory(authentication, takeHistoryPostReq);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "복용 이력 등록 성공/경험치 등록 성공"));

	}

	@GetMapping()
	public ResponseEntity<?> getDateAlarmList(Authentication authentication, @RequestParam String nowDate) {
		try {

			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();

			List<AlarmGetRes> alarmList = alarmService.getDateAlarmList(user, nowDate);

			Map<String, List> map = new HashMap<String, List>();
			map.put("alarmList", alarmList);

			return ResponseEntity.status(200).body(map);

		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
		}
	}

	@GetMapping("/{pageNum}")
	public ResponseEntity<?> getPastAlarmList(Authentication authentication, @PathVariable int pageNum) {
		try {

			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();
			
			AlarmHistoryGetRes alarmHistoryGetRes = alarmService.getPastAlarmList(pageNum, user);

			return ResponseEntity.status(200).body(alarmHistoryGetRes);

		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
		}
	}

	@PostMapping("/ocr")
	public ResponseEntity<?> getOCRMediList(Authentication authentication,
			@RequestBody AlarmOCRPostReq alarmOCRPostReq) {
		try {

			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();
			try {
				List<AlarmOCRRes> mediList = alarmService.getOCRMediList(alarmOCRPostReq.getText());
				Map<String, List> map = new HashMap<String, List>();
				map.put("mediList", mediList);
				return ResponseEntity.status(200).body(map);

			} catch (NullPointerException e) {
				return ResponseEntity.status(404).body(BaseResponseBody.of(404, "입력 text null 오류"));
			}

		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
		}
	}

	@GetMapping("/calendar")
	public ResponseEntity<?> getMonthAlarmList(Authentication authentication, @RequestParam String nowMonth) {
		try {

			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();

			List<AlarmCalendarGetRes> alarmList = alarmService.getMonthAlarmList(user, nowMonth);

			Map<String, List> map = new HashMap<String, List>();
			map.put("alarmList", alarmList);

			return ResponseEntity.status(200).body(map);

		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
		}
	}

	@GetMapping("/main")
	public ResponseEntity<?> getMainAlarmList(Authentication authentication) {
		try {

			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();

			AlarmMainGetRes alarmMainGetRes = alarmService.getMainAlarmList(user);

			return ResponseEntity.status(200).body(alarmMainGetRes);

		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
		}
	}
}
