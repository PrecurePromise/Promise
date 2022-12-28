package com.pjt3.promise.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name="Tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="tag_id")
    int tagId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="alarm_id")
    MediAlarm mediAlarm;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_email")
    User user;

    @Column(name="tag_name")
    String tagName;

    @Builder
    public Tag(MediAlarm mediAlarm, User user, String tagName) {
        this.mediAlarm = mediAlarm;
        this.user = user;
        this.tagName = tagName;
    }
}
