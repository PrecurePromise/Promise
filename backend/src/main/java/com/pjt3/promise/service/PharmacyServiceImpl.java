package com.pjt3.promise.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pjt3.promise.entity.Pharmacy;
import com.pjt3.promise.entity.PharmacyDistanceInterface;
import com.pjt3.promise.repository.PharmacyRepository;
import com.pjt3.promise.repository.PharmacyRepositorySupport;
import com.pjt3.promise.response.PharmacyGetRes;

@Service
public class PharmacyServiceImpl implements PharmacyService {
	
	@Autowired
	PharmacyRepository pharmacyRepository;
	
	@Autowired
	PharmacyRepositorySupport pharmacyRepositorySupport;
	

	@Override
	public List<PharmacyGetRes> getPharmacyListByLatLong(double lat, double lon, int week, String curTime) {
		List<PharmacyGetRes> pharmacyListGetRes = new ArrayList<>();
		List<PharmacyDistanceInterface> pharmacyList = new ArrayList<>();
		
		pharmacyList = pharmacyRepositorySupport.getPharmacyList(lat, lon, week, curTime);
		
		
		for (PharmacyDistanceInterface pharmacy : pharmacyList) {
			Double distance = pharmacy.getDistance() * 1000;
			int intDistance = distance.intValue();

			PharmacyGetRes pharmacyGetRes = PharmacyGetRes.builder()
							.pharmId(pharmacy.getPharmId())
							.pharmName(pharmacy.getPharmName())
							.pharmAddr(pharmacy.getPharmAddr())
							.pharmTel(pharmacy.getPharmTel())
							.pharmLat(pharmacy.getPharmLat())
							.pharmLong(pharmacy.getPharmLong())
							.distance(intDistance)
							.build();
			
			pharmacyListGetRes.add(pharmacyGetRes);
		}
		
		return pharmacyListGetRes;
	}

}