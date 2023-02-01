package com.pjt3.promise.service;

import java.util.List;

import com.pjt3.promise.entity.User;
import com.pjt3.promise.request.UserInfoPutReq;
import com.pjt3.promise.request.UserInsertPostReq;
import com.pjt3.promise.request.UserProfilePostReq;
import com.pjt3.promise.response.ShareUserGetRes;
import com.pjt3.promise.response.UserInfoGetRes;
import org.springframework.security.core.Authentication;

public interface UserService {
	void insertUser(UserInsertPostReq userInsertInfo);
	UserInfoGetRes getUserInfo(Authentication authentication);

	User getUserByUserEmail(String userEmail);
	User getUserByUserNickname(String userNickname);
	void getUserByUserNicknameWithAuth(Authentication authentication, String userNickname);
	User getUserByRefreshToken(String refreshToken);

	void deleteUser(Authentication authentication);
	void update(Authentication authentication, UserInfoPutReq userUpdateInfo);
	void updateProfile(Authentication authentication, UserProfilePostReq userProfileInfo);
	List<ShareUserGetRes> getShareUserList(Authentication authentication, String searchKeyword);
}
