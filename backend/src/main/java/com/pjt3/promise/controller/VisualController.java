package com.pjt3.promise.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pjt3.promise.common.auth.PMUserDetails;
import com.pjt3.promise.common.response.BaseResponseBody;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.response.UsersTagGetRes;
import com.pjt3.promise.service.VisualService;

//@CrossOrigin(
//        origins = { "http://localhost:3000", "https://k5a201.p.ssafy.io/" },
//        allowCredentials = "true",
//        allowedHeaders = "*",
//        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS })
@RequestMapping("/visual")
@RestController
@RequiredArgsConstructor
public class VisualController {
	 
	 private final VisualService visualService;

	 @GetMapping()
	 public ResponseEntity<?> getUsersTagInfo(Authentication authentication){
        try {
        	PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
            User user = userDetails.getUser();
        	
            List<UsersTagGetRes> UsersTagList = visualService.getUsersTagListInfo();

            Map<String, List> map = new HashMap<String, List>();
            map.put("UsersTagList", UsersTagList);

            return ResponseEntity.status(200).body(map);
        } catch (NullPointerException e) {
        	return ResponseEntity.status(420).body(BaseResponseBody.of(420, "만료된 토큰입니다."));   
        } catch(Exception e) {
            return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Internal Server Error"));
        }
    }
}
