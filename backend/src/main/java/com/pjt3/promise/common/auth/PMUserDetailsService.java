package com.pjt3.promise.common.auth;


import com.pjt3.promise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pjt3.promise.entity.User;

// 현재 액세스 토큰으로부터 인증된 유저의 상세정보(활성화 여브 만료, 롤, 유저정보 등) 관련 서비스 정의

@Service
@RequiredArgsConstructor
public class PMUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		User user = userRepository.findUserByUserEmail(userEmail);
		if(user != null) return new PMUserDetails(user);
		
		return null;
	}
	
	
}
