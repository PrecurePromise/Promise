package com.pjt3.promise.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pjt3.promise.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserNickname(String userNickname);
    User findUserByUserEmail(String userEmail);
    User findUserByUserNickname(String userNickname);
    User findUserByRefreshToken(String refreshToken);
    @Transactional
    int deleteUserByUserEmail(String userEmail);
}
