package com.pjt3.promise.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pjt3.promise.common.auth.PMUserDetails;
import com.pjt3.promise.common.response.BaseResponseBody;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.request.UserInfoPutReq;
import com.pjt3.promise.request.UserInsertPostReq;
import com.pjt3.promise.request.UserProfilePostReq;
import com.pjt3.promise.response.ShareUserGetRes;
import com.pjt3.promise.response.UserInfoGetRes;
import com.pjt3.promise.service.PetService;
import com.pjt3.promise.service.UserService;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final PetService petService;

	// 회원가입
	@PostMapping("/signin")
	public ResponseEntity<BaseResponseBody> insertUser(@RequestBody UserInsertPostReq insertInfo){

		userService.insertUser(insertInfo);
		petService.insertPet(insertInfo);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "환영합니다. 회원가입에 성공하셨습니다."));
	}
	
	// 이메일 중복 체크
	@GetMapping("/email/{userEmail}")
	public ResponseEntity<BaseResponseBody> checkDuplicatedEmail(@PathVariable String userEmail){

		userService.getUserByUserEmail(userEmail);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "사용할 수 있는 Email입니다."));
	}
	
	// 닉네임 중복 체크 (회원가입 시)
	@GetMapping("/nickname/{userNickname}")
	public ResponseEntity<BaseResponseBody> checkDuplicatedNickname(@PathVariable String userNickname){

		userService.getUserByUserNickname(userNickname);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "사용할 수 있는 닉네임입니다."));
	}

	// 내 정보 조회
	@GetMapping()
	public ResponseEntity<UserInfoGetRes> getUserInfo(Authentication authentication){
		UserInfoGetRes userInfoGetRes = userService.getUserInfo(authentication);

		return ResponseEntity.status(200).body(userInfoGetRes);
	}
	
	// 닉네임 중복 체크 (가입 후 회원정보 수정 시)
	@GetMapping("/me/nickname/{userNickname}")
	public ResponseEntity<BaseResponseBody> checkDuplicatedNicknameUpdate (Authentication authentication, @PathVariable String userNickname){
		userService.getUserByUserNicknameWithAuth(authentication, userNickname);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "사용할 수 있는 닉네임입니다."));
	}
	
	// 회원 탈퇴
	@DeleteMapping()
	public ResponseEntity<BaseResponseBody> deleteUser (Authentication authentication){

		userService.deleteUser(authentication);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "회원탈퇴에 성공하셨습니다."));

	}
	
	// 회원 정보 수정
	@PutMapping()
	public ResponseEntity<BaseResponseBody> updateUserInfo (Authentication authentication, @RequestBody UserInfoPutReq userUpdateInfo){
		try {
			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();
			
			if (userService.update(user, userUpdateInfo) == 1) {
				return ResponseEntity.status(200).body(BaseResponseBody.of(200, "내 정보가 수정되었습니다."));			
			}
			else if (userService.update(user, userUpdateInfo) == 2) {
				return ResponseEntity.status(409).body(BaseResponseBody.of(409, "중복된 닉네임입니다."));
			}
			else {
				return ResponseEntity.status(404).body(BaseResponseBody.of(404, "업데이트 과정에서 문제가 발생했습니다."));
			}
		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		}
	}
	
	// 내 프로필 사진 수정
	@PutMapping("/profile")
	public ResponseEntity<BaseResponseBody> updateUserProfile (Authentication authentication, @RequestBody UserProfilePostReq userProfileInfo){
		try {
			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();
			
			if (userService.updateProfile(user, userProfileInfo) == 1) {
				return ResponseEntity.status(200).body(BaseResponseBody.of(200, "프로필 사진이 수정되었습니다."));
			}
			else {
				return ResponseEntity.status(400).body(BaseResponseBody.of(400, "프로필 사진 업데이트 과정에서 문제가 발생했습니다."));				
			}
		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));
		}
	}
	
	// 사용자 찾기 (알람 입력 시 - 공유할 사용자 이메일 검색)
	@GetMapping("/sharing")
	public ResponseEntity<List<ShareUserGetRes>> getShareUserList (Authentication authentication, @RequestParam String searchKeyword){
		
		try {
			PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
			User user = userDetails.getUser();
			String userEmail = user.getUserEmail();
			String userNickname = user.getUserNickname();
			
			List<ShareUserGetRes> shareUserGetResList = userService.getShareUserList(searchKeyword, userEmail, userNickname);
			
			if (shareUserGetResList.size() == 0) {
				return ResponseEntity.status(400).body(null);
			}
			
			return ResponseEntity.status(200).body(shareUserGetResList);
			
		} catch (NullPointerException e) {
			return ResponseEntity.status(420).body(null);
		}
		
	}
}