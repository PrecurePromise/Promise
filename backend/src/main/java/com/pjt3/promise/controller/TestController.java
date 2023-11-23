package com.pjt3.promise.controller;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags={"테스트"})
@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping()
    public ResponseEntity<String> test(){
        return ResponseEntity.status(200).body("도커 이미지, 젠킨스 테스트가 정상적으로 작동합니다. 22");
    }

}
