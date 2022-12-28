package com.pjt3.promise.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.*;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Getter
@Table(name="User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(name="user_email")
    String userEmail;
    
    @Column(name="user_password")
    String userPassword;

    @Column(name="user_nickname")
    String userNickname;

    @Column(name="user_profile_url")
    String userProfileUrl;

    @Column(name="user_type")
    int userType;
    
    @Column(name="refresh_token")
    String refreshToken;

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name="user_join_date")
    Date userJoinDate;

    @Column(name="user_join_type")
    int userJoinType;

    public void updateUserProfileUrl(String userProfileUrl){
        this.userProfileUrl = userProfileUrl;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    @Builder
    public User(String userEmail, String userPassword, String userNickname, String userProfileUrl, int userJoinType){
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userNickname = userNickname;
        this.userProfileUrl = userProfileUrl;
        this.userJoinType = userJoinType;
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    Pet pet;

    @JsonManagedReference
    @OneToMany(mappedBy="user")
    List<AlarmShare> alarmShare = new ArrayList<AlarmShare>();

    @JsonManagedReference
    @OneToMany(mappedBy="user")
    List<Tag> tag = new ArrayList<Tag>();

    @JsonManagedReference
    @OneToMany(mappedBy="user")
    List<TakeHistory> takeHistory = new ArrayList<TakeHistory>();

    @JsonManagedReference
    @OneToMany(mappedBy="user")
    List<MediAlarm> mediAlarm = new ArrayList<MediAlarm>();

    @JsonManagedReference
    @OneToMany(mappedBy="user")
    List<Community> community = new ArrayList<Community>();

    @JsonManagedReference
    @OneToMany(mappedBy="user")
    List<CommunityComment> communityComment = new ArrayList<CommunityComment>();
}
