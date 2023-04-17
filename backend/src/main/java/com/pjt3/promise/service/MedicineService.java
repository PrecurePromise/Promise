package com.pjt3.promise.service;

import com.pjt3.promise.response.MediDetailGetRes;
import com.pjt3.promise.response.MediGetRes;
import com.pjt3.promise.response.MediSearchGetRes;

import java.util.List;
import java.util.Map;

public interface MedicineService {
    List<MediGetRes> getMediAutoListInfo(String searchKeyword);
    Map<String, List> getMediSearchListInfo(String searchKeyword);
    MediDetailGetRes getMediDetailInfo(String searchKeyword);
}
