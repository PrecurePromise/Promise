package com.pjt3.promise.entity;

import lombok.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Table(name="Alarm_Share")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmShare {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="as_id")
    int asId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_email")
    User user;
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="send_user_email")
    User sendUser;
    
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
    public AlarmShare(User user, User sendUser, String alarmTitle, int alarmYN, String alarmTime1, String alarmTime2, String alarmTime3, String alarmDayStart, String alarmDayEnd) {
        this.user = user;
        this.sendUser = sendUser;
        this.alarmTitle = alarmTitle;
        this.alarmYN = alarmYN;
        this.alarmTime1 = alarmTime1;
        this.alarmTime2 = alarmTime2;
        this.alarmTime3 = alarmTime3;
        this.alarmDayStart = alarmDayStart;
        this.alarmDayEnd = alarmDayEnd;
    }
    
    @JsonManagedReference
    @OneToMany(mappedBy="alarmShare")
    List<AlarmShareUserMedicine> alarmShareUserMedicine = new ArrayList<AlarmShareUserMedicine>();
    
}
