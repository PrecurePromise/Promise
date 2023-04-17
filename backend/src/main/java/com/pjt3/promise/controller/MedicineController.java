package com.pjt3.promise.controller;

import com.pjt3.promise.common.auth.PMUserDetails;
import com.pjt3.promise.common.response.BaseResponseBody;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.response.MediDetailGetRes;
import com.pjt3.promise.response.MediGetRes;
import com.pjt3.promise.response.MediSearchGetRes;
import com.pjt3.promise.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/medicines")
@RestController
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping("/alarm")
    public ResponseEntity<?> getMediAutoSearchList(@RequestParam String searchKeyword){

        List<MediGetRes> mediList = medicineService.getMediAutoListInfo(searchKeyword);

        return ResponseEntity.status(200).body(mediList);

    }

    @GetMapping("/search")
    public ResponseEntity<?> getMediSearchList(@RequestParam String searchKeyword){

        Map<String, List> mediList = medicineService.getMediSearchListInfo(searchKeyword);

        return ResponseEntity.status(200).body(mediList);

    }

    @GetMapping("/detail/{mediSerialNum}")
    public ResponseEntity<?> getMediDetailList(@PathVariable String mediSerialNum){

        MediDetailGetRes mediInfo = medicineService.getMediDetailInfo(mediSerialNum);

        return ResponseEntity.status(200).body(mediInfo);

    }
}
