package com.pjt3.promise.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pjt3.promise.entity.AlarmShare;
import com.pjt3.promise.entity.AlarmShareUserMedicine;
import com.pjt3.promise.entity.MediAlarm;
import com.pjt3.promise.entity.Tag;
import com.pjt3.promise.entity.TakeHistory;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.entity.UserMedicine;
import com.pjt3.promise.repository.AlarmShareRepository;
import com.pjt3.promise.repository.AlarmShareUserMedicineRepository;
import com.pjt3.promise.repository.MediAlarmRepository;
import com.pjt3.promise.repository.MediAlarmRepositorySupport;
import com.pjt3.promise.repository.MedicineRepository;
import com.pjt3.promise.repository.MedicineRepositorySupport;
import com.pjt3.promise.repository.TagRepository;
import com.pjt3.promise.repository.TakeHistoryRepository;
import com.pjt3.promise.repository.UserMedicineRepository;
import com.pjt3.promise.repository.UserRepository;
import com.pjt3.promise.request.AlarmPostReq;
import com.pjt3.promise.request.AlarmPutReq;
import com.pjt3.promise.request.TakeHistoryPostReq;
import com.pjt3.promise.response.AlarmCalendarGetRes;
import com.pjt3.promise.response.AlarmDetailGetRes;
import com.pjt3.promise.response.AlarmGetRes;
import com.pjt3.promise.response.AlarmHistoryGetRes;
import com.pjt3.promise.response.AlarmMainGetRes;
import com.pjt3.promise.response.AlarmMainListGetRes;
import com.pjt3.promise.response.AlarmOCRRes;

@Service
public class AlarmServiceImpl implements AlarmService {

	private static final int SUCCESS = 1;
	private static final int FAIL = -1;

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	MediAlarmRepository mediAlarmRepository;

	@Autowired
	MedicineRepository medicineRepository;

	@Autowired
	UserMedicineRepository userMedicineRepository;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AlarmShareRepository alarmShareRepository;

	@Autowired
	MediAlarmRepositorySupport mediAlarmRepositorySupport;

	@Autowired
	TakeHistoryRepository takeHistoryRepository;

	@Autowired
	MedicineRepositorySupport medicineRepositorySupport;
	
	@Autowired
	AlarmShareUserMedicineRepository AlarmShareUserMedicineRepository;

	@Transactional
	@Override
	public int insertAlarm(User user, AlarmPostReq alarmPostReq) {

		MediAlarm mediAlarm = null;

		try {

			// 알람 저장
			mediAlarm = mediAlarmSetting(user, alarmPostReq);
			mediAlarmRepository.save(mediAlarm);

			// 약 내역 저장
			userMedicineSetting(mediAlarm, alarmPostReq.getAlarmMediList());

			// 태그 저장
			for (String prevTagName : alarmPostReq.getTagList()) {
				String tagName = prevTagName.replaceAll("\\s", "");
				if(tagName.equals("")) continue;
				Tag tag = new Tag();
				tag.setMediAlarm(mediAlarm);
				tag.setUser(user);
				tag.setTagName(tagName);
				tagRepository.save(tag);
			}

			// 공유 대상자
			for (String sharedEmail : alarmPostReq.getShareEmail()) {

				// 대상자를 찾고
				User sharedUser = userRepository.findUserByUserEmail(sharedEmail);

				// 공유 알람 저장
				AlarmShare alarmShare = new AlarmShare();
				alarmShare.setUser(sharedUser);
				alarmShare.setSendUser(user);
				alarmShare.setAlarmTitle(alarmPostReq.getAlarmTitle());
				alarmShare.setAlarmYN(1);
				alarmShare.setAlarmTime1(alarmPostReq.getAlarmTime1());
				alarmShare.setAlarmTime2(alarmPostReq.getAlarmTime2());
				alarmShare.setAlarmTime3(alarmPostReq.getAlarmTime3());
				alarmShare.setAlarmDayStart(alarmPostReq.getAlarmDayStart());
				alarmShare.setAlarmDayEnd(alarmPostReq.getAlarmDayEnd());

				alarmShareRepository.save(alarmShare);
				
				// 알람 공유 약 저장
				alarmShareUserMedicineSetting(alarmShare, alarmPostReq.getAlarmMediList());
			}

			return mediAlarm.getAlarmId();

		} catch (Exception e) {
			return FAIL;
		}
	}

	public MediAlarm mediAlarmSetting(User user, AlarmPostReq alarmPostReq) {

		MediAlarm mediAlarm = MediAlarm.builder()
			.user(user)
			.alarmTitle(alarmPostReq.getAlarmTitle())
			.build();

		try {
			mediAlarm.initAlarmDayStart(alarmPostReq.getAlarmDayStart());
			mediAlarm.initAlarmDayEnd(alarmPostReq.getAlarmDayEnd());
			mediAlarm.initAlarmYN(alarmPostReq.getAlarmYN());
			if (alarmPostReq.getAlarmYN() == 1) {
				mediAlarm.initAlarmTime1(alarmPostReq.getAlarmTime1());
				mediAlarm.initAlarmTime2(alarmPostReq.getAlarmTime2());
				mediAlarm.initAlarmTime3(alarmPostReq.getAlarmTime3());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mediAlarm;
	}

	public void alarmShareUserMedicineSetting(AlarmShare alarmShare, List<String> alarmMediList) {
		for (String mediName : alarmMediList) {

			AlarmShareUserMedicine alarmShareUserMedicine = new AlarmShareUserMedicine();
			alarmShareUserMedicine.setAlarmShare(alarmShare);
			alarmShareUserMedicine.setAsumName(mediName);
			alarmShareUserMedicine.setMedicine(medicineRepository.findMedicineByMediName(mediName));

			AlarmShareUserMedicineRepository.save(alarmShareUserMedicine);
		}

	}
	
	public void userMedicineSetting(MediAlarm mediAlarm, List<String> alarmMediList) {
		for (String userMediName : alarmMediList) {

			UserMedicine userMedicine = UserMedicine.builder()
					.mediAlarm(mediAlarm)
					.medicine(medicineRepository.findMedicineByMediName(userMediName))
					.umName(userMediName).build();

			userMedicineRepository.save(userMedicine);
		}

	}

	@Override
	public int updateAlarm(User user, AlarmPutReq alarmPutReq) {

		MediAlarm mediAlarm = null;

		try {
			mediAlarm = mediAlarmRepository.findMediAlarmByAlarmId(alarmPutReq.getAlarmId());

			tagRepository.deleteByMediAlarmAlarmId(alarmPutReq.getAlarmId());
			userMedicineRepository.deleteByMediAlarmAlarmId(alarmPutReq.getAlarmId());

			mediAlarm.initUser(user);
			mediAlarm.initAlarmTitle(alarmPutReq.getAlarmTitle());
			mediAlarm.initAlarmDayStart(alarmPutReq.getAlarmDayStart());
			mediAlarm.initAlarmDayEnd(alarmPutReq.getAlarmDayEnd());
			mediAlarm.initAlarmYN(alarmPutReq.getAlarmYN());
			if (alarmPutReq.getAlarmYN() == 1) {
				mediAlarm.initAlarmTime1(alarmPutReq.getAlarmTime1());
				mediAlarm.initAlarmTime2(alarmPutReq.getAlarmTime2());
				mediAlarm.initAlarmTime3(alarmPutReq.getAlarmTime3());
			}
			mediAlarmRepository.save(mediAlarm);

			userMedicineSetting(mediAlarm, alarmPutReq.getAlarmMediList());

			for (String prevTagName : alarmPutReq.getTagList()) {
				String tagName = prevTagName.replaceAll("\\s", "");
				if(tagName.equals("")) continue;
				Tag tag = new Tag();
				tag.setMediAlarm(mediAlarm);
				tag.setUser(user);
				tag.setTagName(tagName);
				tagRepository.save(tag);
			}

			return SUCCESS;

		} catch (Exception e) {
			return FAIL;
		}
	}

	@Override
	public int deleteAlarm(int alarmId) {
		try {

			MediAlarm mediAlarm = mediAlarmRepository.findMediAlarmByAlarmId(alarmId);

			mediAlarmRepository.delete(mediAlarm);

			return SUCCESS;
		} catch (Exception e) {
			return FAIL;
		}
	}

	@Override
	public AlarmDetailGetRes getAlarmInfo(int alarmId) {
		return mediAlarmRepositorySupport.getAlarmInfo(alarmId);
	}

	@Override
	public int insertTakeHistory(User user, TakeHistoryPostReq takeHistoryPostReq) {
		try {
			TakeHistory takeHistory = new TakeHistory();
			takeHistory.setUser(user);
			takeHistory.setMediAlarm(mediAlarmRepository.findMediAlarmByAlarmId(takeHistoryPostReq.getAlarmId()));
			takeHistory.setThYN(takeHistoryPostReq.getThYN());
			if (takeHistoryPostReq.getThYN() == 1) {
				takeHistory.setThTime(Timestamp.valueOf(LocalDateTime.now()));
			}

			takeHistoryRepository.save(takeHistory);

			return SUCCESS;
		} catch (Exception e) {
			return FAIL;
		}
	}

	@Override
	public List<AlarmGetRes> getDateAlarmList(User user, String nowDate) {

		LocalDate now = LocalDate.parse(nowDate);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String findDate = now.format(formatter);

		List<AlarmGetRes> alarmList = mediAlarmRepositorySupport.getDateAlarmList(user, findDate);
		return alarmList;

	}

	@Override
	public AlarmHistoryGetRes getPastAlarmList(int pageNum, User user) {

		AlarmHistoryGetRes alarmHistoryGetRes = new AlarmHistoryGetRes();
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String today = formatter.format(c.getTime());
		
		int limit = 8;

		int total = mediAlarmRepositorySupport.getTotalCountPastAlarmList(today, user);
		int totalPageCnt = calcTotalPageCnt(total, limit);
		int offset = (pageNum-1)*limit;
		alarmHistoryGetRes.setTotalPageCnt(totalPageCnt);
		
		List<AlarmGetRes> alarmList = mediAlarmRepositorySupport.getPastAlarmList(today, user, limit, offset);
		alarmHistoryGetRes.setAlarmList(alarmList);
		
		return alarmHistoryGetRes;
	}

    private int calcTotalPageCnt(int total, int limit) {
        int totalPageCnt = 0;
        if (total % limit > 0) totalPageCnt = total / limit + 1;
        else totalPageCnt = total / limit;
        return totalPageCnt;
    }
	
	@Override
	public List<AlarmOCRRes> getOCRMediList(String text) {
		String pattern1 = "^[0-9]*$";
		String pattern2 = "^[a-zA-Z]*$";
		String[] textList = text.split(" ");
		HashSet<AlarmOCRRes> findMediList = new HashSet<AlarmOCRRes>();
		for (String str : textList) {
			str = str.replaceAll(" ", "");

			// 예외 조건 확인 후 추가 필요
			if (str == null || str.equals("") || str.equals(" ")) continue;
			if (str.length() == 0 || str.length() == 1) continue;
			if ((!str.equals("자모") && !str.equals("뇌선") && !str.equals("얄액") && !str.equals("쿨정")) && str.length() == 2) continue;
			if (Pattern.matches(pattern1, str) || Pattern.matches(pattern2, str)) continue;
			if (str.length() == 3 && str.equals("서방정")) continue;
			

			List<AlarmOCRRes> mediList = medicineRepositorySupport.getOCRMediListInfo(str);
			for (AlarmOCRRes medi : mediList) {
				findMediList.add(medi);
			}

		}

		return new ArrayList<AlarmOCRRes>(findMediList);
	}

	@Override
	public List<AlarmCalendarGetRes> getMonthAlarmList(User user, String nowMonth) {
		StringTokenizer st = new StringTokenizer(nowMonth, "-");
		
		Calendar c = Calendar.getInstance();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		c.set(Calendar.YEAR, Integer.parseInt(st.nextToken()));
		c.set(Calendar.MONTH, Integer.parseInt(st.nextToken())-1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		String firstDay = formatter.format(c.getTime());
		
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		String lastDay = formatter.format(c.getTime());

		List<AlarmCalendarGetRes> calendarAlarmList =  mediAlarmRepositorySupport.getMonthAlarmList(user, firstDay, lastDay);

		return calendarAlarmList;
	}

	@Override
	public AlarmMainGetRes getMainAlarmList(User user) {
		
		AlarmMainGetRes alarmMainGetRes = new AlarmMainGetRes();
		LocalDate now = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String today = now.format(formatter);
		List<AlarmMainListGetRes> alarmList = mediAlarmRepositorySupport.getMainAlarmList(user, today);
		
		alarmMainGetRes.setAlarmList(alarmList);
		long count = mediAlarmRepository.countByUser(user);
		if(count > 0) alarmMainGetRes.setPreAlarm(true);
		else alarmMainGetRes.setPreAlarm(false);
		
		return alarmMainGetRes;
	}
}
