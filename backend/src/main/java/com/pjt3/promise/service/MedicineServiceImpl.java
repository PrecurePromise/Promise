package com.pjt3.promise.service;

import com.pjt3.promise.entity.Medicine;
import com.pjt3.promise.repository.MedicineRepository;
import com.pjt3.promise.repository.MedicineRepositorySupport;
import com.pjt3.promise.response.MediDetailGetRes;
import com.pjt3.promise.response.MediGetRes;
import com.pjt3.promise.response.MediSearchGetRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("MedicineService")
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService{

    private final MedicineRepositorySupport medicineRepositorySupport;

    @Override
    public List<MediGetRes> getMediAutoListInfo(String searchKeyword) {
    	List<MediGetRes> mediList = medicineRepositorySupport.getMediAutoListInfo(searchKeyword);
        return mediList;
    }

    @Override
    public Map<String, List> getMediSearchListInfo(String searchKeyword) {
        List<MediSearchGetRes> mediList = medicineRepositorySupport.getMediSearchListInfo(searchKeyword);

        Map<String, List> map = new HashMap<String, List>();
        map.put("mediList", mediList);

        return map;
    }

    @Override
    public MediDetailGetRes getMediDetailInfo(String mediSerialNum) {
    	MediDetailGetRes mediInfo = medicineRepositorySupport.getMediDetailInfo(mediSerialNum);
        return mediInfo;
    }

}
