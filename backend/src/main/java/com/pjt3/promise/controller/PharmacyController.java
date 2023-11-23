package com.pjt3.promise.controller;

import java.util.List;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pjt3.promise.response.PharmacyGetRes;
import com.pjt3.promise.service.PharmacyService;

@Api(tags={"약국"})
@RequestMapping("/pharmacies")
@RestController
@RequiredArgsConstructor
public class PharmacyController {
	
	private final PharmacyService pharmacyService;
	
	@GetMapping("")
	public ResponseEntity<List<PharmacyGetRes>> getPharmacyListByLatLon(@RequestParam double lat, double lon, int week, String curTime){

		List<PharmacyGetRes> pharmacyList = pharmacyService.getPharmacyListByLatLong(lat, lon, week, curTime);

		return ResponseEntity.status(200).body(pharmacyList);
	}
	
}
