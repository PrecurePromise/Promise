package com.pjt3.promise.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Table(name="Take_History")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TakeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="th_id")
    int thId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="alarm_id")
    MediAlarm mediAlarm;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_email")
    User user;

    @Column(name="th_time")
    Date thTime;

    @Column(name="th_YN")
    int thYN;

    @Builder
    public TakeHistory(User user, MediAlarm mediAlarm, int thYN, Date thTime) {
        this.user = user;
        this.mediAlarm = mediAlarm;
        this.thYN = thYN;
        this.thTime = thTime;
    }

    public void initThTime(Date thTime) {
        this.thTime = thTime;
    }
}
