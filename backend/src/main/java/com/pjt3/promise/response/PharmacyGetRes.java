package com.pjt3.promise.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PharmacyGetRes {
	
	int pharmId;
	String pharmName;
	String pharmTel;
	String pharmAddr;
	Double pharmLat;
	Double pharmLong;
	int distance;

	@Builder
	public PharmacyGetRes (int pharmId, String pharmName, String pharmTel, String pharmAddr, Double pharmLat, Double pharmLong, int distance){
		this.pharmId = pharmId;
		this.pharmName = pharmName;
		this.pharmTel = pharmTel;
		this.pharmAddr = pharmAddr;
		this.pharmLat = pharmLat;
		this.pharmLong = pharmLong;
		this.distance = distance;
	}
}
