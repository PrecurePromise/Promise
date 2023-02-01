package com.pjt3.promise.service;

import java.util.List;

import com.pjt3.promise.common.auth.PMUserDetails;
import com.pjt3.promise.exception.CustomException;
import com.pjt3.promise.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pjt3.promise.entity.Pet;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.repository.PetRepository;
import com.pjt3.promise.repository.UserRepository;
import com.pjt3.promise.repository.UserRepositorySupport;
import com.pjt3.promise.request.UserInfoPutReq;
import com.pjt3.promise.request.UserInsertPostReq;
import com.pjt3.promise.request.UserProfilePostReq;
import com.pjt3.promise.response.ShareUserGetRes;
import com.pjt3.promise.response.UserInfoGetRes;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final PetRepository petRepository;
	private final UserRepositorySupport userRepositorySupport;

	@Override
	public void insertUser(UserInsertPostReq userInsertInfo) {
		boolean existedUserByUserEmail = userRepository.existsByUserEmail(userInsertInfo.getUserEmail());
		boolean existedUserByUserNickname = userRepository.existsByUserNickname(userInsertInfo.getUserNickname());

		if (existedUserByUserEmail && existedUserByUserNickname) {
			throw new CustomException(ErrorCode.DUPLICATED_EMAIL_NICKNAME);
		} else if (existedUserByUserNickname) {
			throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
		} else if (existedUserByUserEmail) {
			throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
		} else {
			User user = User.builder()
					.userEmail(userInsertInfo.getUserEmail())
					.userPassword(passwordEncoder.encode(userInsertInfo.getUserPassword()))
					.userNickname(userInsertInfo.getUserNickname())
					.userProfileUrl(userInsertInfo.getUserProfileUrl())
					.userJoinType(userInsertInfo.getUserJoinType())
					.build();

			userRepository.save(user);
		}
	}

	@Override
	public UserInfoGetRes getUserInfo(Authentication authentication) {
		UserInfoGetRes userInfoGetRes = new UserInfoGetRes();

		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
		User user = userDetails.getUser();
		Pet pet = petRepository.findPetByUser(user);

		userInfoGetRes.setStatusCode(200);
		userInfoGetRes.setMessage("조회에 성공했습니다.");
		userInfoGetRes.setPetName(pet.getPetName());
		userInfoGetRes.setPetLevel(pet.getPetLevel());

		BeanUtils.copyProperties(user, userInfoGetRes);

		return userInfoGetRes;
	}

	@Override
	public User getUserByUserEmail(String userEmail) {
		User user = userRepository.findUserByUserEmail(userEmail);

		if (user != null) {
			throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
		} else {
			return user;
		}
	}

	@Override
	public User getUserByUserNickname(String userNickname) {
		User user = userRepository.findUserByUserNickname(userNickname);

		if (user != null) {
			throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
		} else {
			return user;
		}
	}

	@Override
	public void getUserByUserNicknameWithAuth(Authentication authentication, String userNickname) {
		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();

		User authUser = userDetails.getUser();
		String authUserNickname = authUser.getUserNickname();

		User checkUser = userRepository.findUserByUserNickname(userNickname);

		if (authUserNickname.equals(userNickname)) {
			throw new CustomException(ErrorCode.DUPLICATED_NICKNAME_OWN);
		} else {
			if (checkUser != null) {
				throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
			}
		}
	}

	@Override
	public User getUserByRefreshToken(String refreshToken) {
		User user = userRepository.findUserByRefreshToken(refreshToken);
		return user;
	}

	@Override
	public void deleteUser(Authentication authentication) {

		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
		String userEmail = userDetails.getUsername();

		int deleteRes = userRepository.deleteUserByUserEmail(userEmail);

		if (deleteRes != 1) {
			throw new CustomException(ErrorCode.CANNOT_DELETE_USER);
		}

	}

	@Override
	public void update(Authentication authentication, UserInfoPutReq userUpdateInfo) {

		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
		User user = userDetails.getUser();

		String userNickname = userUpdateInfo.getUserNickname();
		String petName = userUpdateInfo.getPetName();
		Pet pet = petRepository.findPetByUser(user);

		getUserByUserNicknameWithAuth(authentication, userNickname);

		pet.givePetName(petName);
		user.updateNickname(userNickname);
		System.out.println("userNickname : " + userNickname);
		userRepository.save(user);
	}

	@Override
	public int updateProfile(User user, UserProfilePostReq userProfileInfo) {
		try {
			String userProfileUrl = userProfileInfo.getUserProfileUrl();
			user.updateUserProfileUrl(userProfileUrl);
			userRepository.save(user);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<ShareUserGetRes> getShareUserList(String searchKeyword, String userEmail, String userNickname) {
		List<ShareUserGetRes> shareUserList = userRepositorySupport.getShareUserList(searchKeyword);

		for (ShareUserGetRes shareUserGetRes : shareUserList) {
			if (shareUserGetRes.getUserEmail().equals(userEmail) && shareUserGetRes.getUserNickname().equals(userNickname)) {
				shareUserList.remove(shareUserGetRes);
				break;
			}
		}

		return shareUserList;
	}

}