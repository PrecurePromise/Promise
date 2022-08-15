package com.pjt3.promise.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Getter
@Table(name="Pet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="pet_id")
    int petId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_email")
    User user;

    @Column(name="pet_name")
    String petName;

    @Column(name="pet_level")
    int petLevel;

    @Column(name="pet_exp")
    int petExp;

    public void updatePetLevel(int petLevel){
        this.petLevel = petLevel;
    }

    public void updatePetExp(int petExp){
        this.petExp = petExp;
    }

    public void givePetName(String petName){
        this.petName = petName;
    }

    @Builder
    public Pet(User user, String petName, int petLevel){
        this.user = user;
        this.petName = petName;
        this.petLevel = petLevel;
    }
}