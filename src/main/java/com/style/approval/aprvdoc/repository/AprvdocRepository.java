package com.style.approval.aprvdoc.repository;

import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AprvdocRepository extends JpaRepository<AprvdocEntity, Long> {
    Optional<AprvdocEntity> findByDocNo(String docNo);
    List<AprvdocEntity> findByRegUserOrderByRegDateDesc(UserEntity regUser);
    List<AprvdocEntity> findByRegUserAndStatusOrderByRegDateDesc(UserEntity regUser, String status);

    @Query("select doc from AprvdocEntity doc join fetch doc.aprvlines line where line.aprvUser = :user and line.status = :status")
    List<AprvdocEntity> findInbox(UserEntity user, String status);

    @Query("select doc from AprvdocEntity doc join fetch doc.aprvlines line where line.aprvUser = :user and doc.status in (:status)")
    List<AprvdocEntity> findArchive(UserEntity user, List<String> status);

}
