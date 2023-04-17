package com.pjt3.promise.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pjt3.promise.repository.VisualRepositorySupport;
import com.pjt3.promise.response.UsersTagGetRes;

@Service
@RequiredArgsConstructor
public class VisualServiceimpl implements VisualService {

	private final VisualRepositorySupport visualRepositorySupport;
	
	@Override
	public List<UsersTagGetRes> getUsersTagListInfo() {
		List<UsersTagGetRes> usersTagList = visualRepositorySupport.getUsersTagListInfo();
		return usersTagList;
	}

}
