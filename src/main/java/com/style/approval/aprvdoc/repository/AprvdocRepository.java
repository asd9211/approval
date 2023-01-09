package com.style.approval.aprvdoc.repository;

import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AprvdocRepository extends JpaRepository<AprvdocEntity, Long> {
    Optional<AprvdocEntity> findByDocNo(String docNo);
    List<AprvdocEntity> findByRegUserOrderByRegDateDesc(UserEntity regUser);
    List<AprvdocEntity> findByRegUserAndStatusOrderByRegDateDesc(UserEntity regUser, String status);

}
