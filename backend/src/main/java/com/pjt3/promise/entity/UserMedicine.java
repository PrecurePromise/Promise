package com.pjt3.promise.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name="User_Medicine")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMedicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="um_id")
    int umId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="alarm_id")
    MediAlarm mediAlarm;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="medi_serial_num")
    Medicine medicine;

    @Column(name="um_name")
    String umName;

    @Builder
    public UserMedicine(MediAlarm mediAlarm, Medicine medicine, String umName) {
        this.mediAlarm = mediAlarm;
        this.medicine = medicine;
        this.umName = umName;
    }
}
