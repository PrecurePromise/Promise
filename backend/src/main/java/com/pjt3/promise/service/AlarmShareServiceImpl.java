package com.pjt3.promise.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.pjt3.promise.common.auth.PMUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.pjt3.promise.entity.AlarmShare;
import com.pjt3.promise.entity.AlarmShareUserMedicine;
import com.pjt3.promise.entity.MediAlarm;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.entity.UserMedicine;
import com.pjt3.promise.repository.AlarmShareRepository;
import com.pjt3.promise.repository.AlarmShareRepositorySupport;
import com.pjt3.promise.repository.AlarmShareUserMedicineRepository;
import com.pjt3.promise.repository.MediAlarmRepository;
import com.pjt3.promise.repository.UserMedicineRepository;
import com.pjt3.promise.request.AlarmShareAcceptReq;
import com.pjt3.promise.response.AlarmShareGetRes;

@Service
@RequiredArgsConstructor
public class AlarmShareServiceImpl implements AlarmShareService {

	private static final int SUCCESS = 1;
	private static final int FAIL = -1;

	private final AlarmShareRepository alarmShareRepository;
	private final MediAlarmRepository mediAlarmRepository;
	private final UserMedicineRepository userMedicineRepository;
	private final AlarmShareUserMedicineRepository alarmShareUserMedicineRepository;
	private final AlarmShareRepositorySupport alarmShareRepositorySupport;

	@Override
	public Map<String, List> getAlarmShareMap(Authentication authentication) {
		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
		User user = userDetails.getUser();

		List<AlarmShareGetRes> alarmShareList = alarmShareRepositorySupport.getAlarmInfo(user);

		Map<String, List> map = new HashMap<>();
		map.put("alarmShareList", alarmShareList);

		return map;

	}

	@Transactional
	@Override
	public int acceptAlarmShare(Authentication authentication, AlarmShareAcceptReq alarmShareAcceptReq) {

		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
		User user = userDetails.getUser();

		try {

			AlarmShare alarmShare = alarmShareRepository.findByAsId(alarmShareAcceptReq.getAsId());

			MediAlarm mediAlarm = MediAlarm.builder()
					.user(user)
					.alarmTitle(alarmShare.getAlarmTitle())
					.alarmYN(alarmShare.getAlarmYN())
					.alarmTime1(alarmShare.getAlarmTime1())
					.alarmTime2(alarmShare.getAlarmTime2())
					.alarmTime3(alarmShare.getAlarmTime3())
					.alarmDayStart(alarmShare.getAlarmDayStart())
					.alarmDayEnd(alarmShare.getAlarmDayEnd())
					.build();
			
			mediAlarmRepository.save(mediAlarm);
			
			List<AlarmShareUserMedicine> alarmShareUserMedicineList = alarmShareUserMedicineRepository.findByAlarmShare(alarmShare);
			
			for (AlarmShareUserMedicine alarmShareUserMedicine : alarmShareUserMedicineList) {

				UserMedicine userMedicine = UserMedicine.builder()
						.mediAlarm(mediAlarm)
						.medicine(alarmShareUserMedicine.getMedicine())
						.umName(alarmShareUserMedicine.getAsumName()).build();

				userMedicineRepository.save(userMedicine);
			}
			
			alarmShareRepository.delete(alarmShare);
			
			return SUCCESS;

		} catch (Exception e) {
			e.printStackTrace();
			return FAIL;
		}
	}
	
	@Transactional
	@Override
	public int rejectAlarmShare(int asId) {

		try {

			alarmShareRepository.deleteById(asId);
			return SUCCESS;

		} catch (Exception e) {
			return FAIL;
		}
	}

}
