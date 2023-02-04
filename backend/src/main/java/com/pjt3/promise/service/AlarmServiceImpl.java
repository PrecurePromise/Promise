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

import com.pjt3.promise.common.auth.PMUserDetails;
import com.pjt3.promise.exception.CustomException;
import com.pjt3.promise.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

	private static final int SUCCESS = 1;
	private static final int FAIL = -1;

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	private final MediAlarmRepository mediAlarmRepository;
	private final MedicineRepository medicineRepository;
	private final UserMedicineRepository userMedicineRepository;
	private final TagRepository tagRepository;
	private final PetService petService;
	private final UserRepository userRepository;
	private final AlarmShareRepository alarmShareRepository;
	private final TakeHistoryRepository takeHistoryRepository;
	private final AlarmShareUserMedicineRepository AlarmShareUserMedicineRepository;
	private final MedicineRepositorySupport medicineRepositorySupport;
	private final MediAlarmRepositorySupport mediAlarmRepositorySupport;

	@Transactional
	@Override
	public int insertAlarm(Authentication authentication, AlarmPostReq alarmPostReq) {

		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
		User user = userDetails.getUser();

		// 알람 저장
		MediAlarm mediAlarm = mediAlarmSetting(user, alarmPostReq);

		MediAlarm resMediAlarm = mediAlarmRepository.save(mediAlarm);
		if(resMediAlarm == null) throw new CustomException(ErrorCode.CANNOT_INSERT_ALARM);

		// 약 내역 저장
		userMedicineSetting(resMediAlarm, alarmPostReq.getAlarmMediList());

		// 태그 저장
		for (String prevTagName : alarmPostReq.getTagList()) {
			String tagName = prevTagName.replaceAll("\\s", "");
			if(tagName.equals("")) continue;

			Tag tag = Tag.builder()
					.mediAlarm(resMediAlarm)
					.user(user)
					.tagName(tagName)
					.build();

			Tag resTag = tagRepository.save(tag);

			if(resTag == null) throw new CustomException(ErrorCode.CANNOT_INSERT_ALARM_TAG);
		}

		// 공유 대상자
		for (String sharedEmail : alarmPostReq.getShareEmail()) {

			// 대상자를 찾고
			User sharedUser = userRepository.findUserByUserEmail(sharedEmail);
			if(sharedUser == null) throw new CustomException(ErrorCode.CANNOT_FOUND_USER);

			// 공유 알람 저장
			AlarmShare alarmShare = AlarmShare.builder()
					.user(sharedUser)
					.sendUser(user)
					.alarmTitle(alarmPostReq.getAlarmTitle())
					.alarmYN(1)
					.alarmTime1(alarmPostReq.getAlarmTime1())
					.alarmTime2(alarmPostReq.getAlarmTime2())
					.alarmTime3(alarmPostReq.getAlarmTime3())
					.alarmDayStart(alarmPostReq.getAlarmDayStart())
					.alarmDayEnd(alarmPostReq.getAlarmDayEnd())
					.build();

			AlarmShare resAlarmShare = alarmShareRepository.save(alarmShare);
			if(resAlarmShare == null) throw new CustomException(ErrorCode.CANNOT_INSERT_ALARM_SHARE);

			// 알람 공유 약 저장
			alarmShareUserMedicineSetting(alarmShare, alarmPostReq.getAlarmMediList());
		}
		petService.increasePetExp(3, user);

		return mediAlarm.getAlarmId();
	}

	public MediAlarm mediAlarmSetting(User user, AlarmPostReq alarmPostReq) {

		MediAlarm mediAlarm = MediAlarm.builder()
			.user(user)
			.alarmTitle(alarmPostReq.getAlarmTitle())
			.build();

		mediAlarm.initAlarmDayStart(alarmPostReq.getAlarmDayStart());
		mediAlarm.initAlarmDayEnd(alarmPostReq.getAlarmDayEnd());
		mediAlarm.initAlarmYN(alarmPostReq.getAlarmYN());
		if (alarmPostReq.getAlarmYN() == 1) {
			mediAlarm.initAlarmTime1(alarmPostReq.getAlarmTime1());
			mediAlarm.initAlarmTime2(alarmPostReq.getAlarmTime2());
			mediAlarm.initAlarmTime3(alarmPostReq.getAlarmTime3());
		}

		return mediAlarm;
	}

	public void alarmShareUserMedicineSetting(AlarmShare alarmShare, List<String> alarmMediList) {
		for (String asumMediName : alarmMediList) {

			AlarmShareUserMedicine alarmShareUserMedicine = AlarmShareUserMedicine.builder()
					.alarmShare(alarmShare)
					.medicine(medicineRepository.findMedicineByMediName(asumMediName))
					.asumName(asumMediName)
					.build();
			AlarmShareUserMedicine resAlarmShareUserMedicine =  AlarmShareUserMedicineRepository.save(alarmShareUserMedicine);
			if(resAlarmShareUserMedicine == null) throw new CustomException(ErrorCode.CANNOT_INSERT_ALARM_SHARE_MEDI);
		}

	}
	
	public void userMedicineSetting(MediAlarm mediAlarm, List<String> alarmMediList) {
		for (String userMediName : alarmMediList) {

			UserMedicine userMedicine = UserMedicine.builder()
					.mediAlarm(mediAlarm)
					.medicine(medicineRepository.findMedicineByMediName(userMediName))
					.umName(userMediName).build();

			UserMedicine resUserMedicine = userMedicineRepository.save(userMedicine);
			if(resUserMedicine == null) throw new CustomException(ErrorCode.CANNOT_INSERT_ALARM_MEDI);
		}

	}

	@Transactional
	@Override
	public void updateAlarm(Authentication authentication, AlarmPutReq alarmPutReq) {

		PMUserDetails userDetails = (PMUserDetails) authentication.getDetails();
		User user = userDetails.getUser();

		MediAlarm mediAlarm = mediAlarmRepository.findMediAlarmByAlarmId(alarmPutReq.getAlarmId());
		if(mediAlarm == null) throw new CustomException(ErrorCode.CANNOT_FIND_ALARM);

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
		MediAlarm resMediAlarm = mediAlarmRepository.save(mediAlarm);
		if(resMediAlarm == null) throw new CustomException(ErrorCode.CANNOT_UPDATE_ALARM);

		userMedicineSetting(mediAlarm, alarmPutReq.getAlarmMediList());

		for (String prevTagName : alarmPutReq.getTagList()) {
			String tagName = prevTagName.replaceAll("\\s", "");
			if(tagName.equals("")) continue;

			Tag tag = Tag.builder()
					.mediAlarm(mediAlarm)
					.user(user)
					.tagName(tagName)
					.build();

			Tag resTag = tagRepository.save(tag);
			if(resTag == null) throw new CustomException(ErrorCode.CANNOT_UPDATE_ALARM_TAG);
		}
	}

	@Transactional
	@Override
	public void deleteAlarm(int alarmId) {

		MediAlarm mediAlarm = mediAlarmRepository.findMediAlarmByAlarmId(alarmId);
		if(mediAlarm == null) throw new CustomException(ErrorCode.CANNOT_FIND_ALARM);

		int res = mediAlarmRepository.deleteMediAlarmByAlarmId(mediAlarm.getAlarmId());
		if(res != 1) throw new CustomException(ErrorCode.CANNOT_DELETE_ALARM);

	}

	@Override
	public AlarmDetailGetRes getAlarmInfo(int alarmId) {

		AlarmDetailGetRes alarmDetailGetRes = mediAlarmRepositorySupport.getAlarmInfo(alarmId);
		if(alarmDetailGetRes == null) throw new CustomException(ErrorCode.CANNOT_FIND_ALARM);

		return alarmDetailGetRes;
	}

	@Override
	public int insertTakeHistory(User user, TakeHistoryPostReq takeHistoryPostReq) {
		try {
			TakeHistory takeHistory = TakeHistory.builder()
					.user(user)
					.mediAlarm(mediAlarmRepository.findMediAlarmByAlarmId(takeHistoryPostReq.getAlarmId()))
					.thYN(takeHistoryPostReq.getThYN())
					.build();
			if (takeHistoryPostReq.getThYN() == 1) {
				takeHistory.initThTime(Timestamp.valueOf(LocalDateTime.now()));

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
