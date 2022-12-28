package com.pjt3.promise.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;

@Entity
@Getter
@Table(name="Alarm_Share_User_Medicine")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmShareUserMedicine {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="asum_id")
    int asunId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="as_id")
    AlarmShare alarmShare;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="medi_serial_num")
    Medicine medicine;

    @Column(name="asum_name")
    String asumName;

    @Builder
    public AlarmShareUserMedicine(AlarmShare alarmShare, Medicine medicine, String asumName) {
        this.alarmShare = alarmShare;
        this.medicine = medicine;
        this.asumName = asumName;
    }

}
