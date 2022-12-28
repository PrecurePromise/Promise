package com.pjt3.promise.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;

@Entity
@Getter
@Table(name="Medi_Alarm")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MediAlarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="alarm_id")
    int alarmId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_email")
    User user;

    @Column(name="alarm_title")
    String alarmTitle;

    @Column(name="alarm_YN")
    int alarmYN;

    @Column(name="alarm_time1")
    String alarmTime1;
    
    @Column(name="alarm_time2")
    String alarmTime2;
    
    @Column(name="alarm_time3")
    String alarmTime3;

    @Column(name="alarm_day_start")
    String alarmDayStart;

    @Column(name="alarm_day_end")
    String alarmDayEnd;

    @Builder
    public MediAlarm(User user, String alarmTitle, int alarmYN, String alarmTime1, String alarmTime2, String alarmTime3, String alarmDayStart, String alarmDayEnd) {
        this.user = user;
        this.alarmTitle = alarmTitle;
        this.alarmYN = alarmYN;
        this.alarmTime1 = alarmTime1;
        this.alarmTime2 = alarmTime2;
        this.alarmTime3 = alarmTime3;
        this.alarmDayStart = alarmDayStart;
        this.alarmDayEnd = alarmDayEnd;
    }

    public void initUser(User user) {
        this.user = user;
    }
    public void initAlarmTitle(String alarmTitle) {
        this.alarmTitle = alarmTitle;
    }
    public void initAlarmDayStart(String alarmDayStart) {
        this.alarmDayStart = alarmDayStart;
    }
    public void initAlarmDayEnd(String alarmDayEnd) {
        this.alarmDayEnd = alarmDayEnd;
    }
    public void initAlarmYN(int alarmYN) {
        this.alarmYN = alarmYN;
    }
    public void initAlarmTime1(String alarmTime1) {
        this.alarmTime1 = alarmTime1;
    }
    public void initAlarmTime2(String alarmTime2) {
        this.alarmTime2 = alarmTime2;
    }
    public void initAlarmTime3(String alarmTime3) {
        this.alarmTime3 = alarmTime3;
    }

    @JsonManagedReference
    @OneToMany(mappedBy="mediAlarm")
    List<TakeHistory> takeHistory = new ArrayList<TakeHistory>();

    @JsonManagedReference
    @OneToMany(mappedBy="mediAlarm")
    List<UserMedicine> userMedicine = new ArrayList<UserMedicine>();

    @JsonManagedReference
    @OneToMany(mappedBy="mediAlarm")
    List<Tag> tag = new ArrayList<Tag>();
}
