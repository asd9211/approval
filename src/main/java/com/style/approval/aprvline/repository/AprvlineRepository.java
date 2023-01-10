package com.style.approval.aprvline.repository;

import com.style.approval.aprvline.entity.AprvlineEntity;
import com.style.approval.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AprvlineRepository extends JpaRepository<AprvlineEntity, Long> {
    List<AprvlineEntity> findByDocNoOrderBySeqNoAsc(String docNo);
    Optional<AprvlineEntity> findByDocNoAndSeqNo(String docNo, Long seqNo);
    Optional<AprvlineEntity> findByDocNoAndAprvUser(String docNo, UserEntity aprvUser);

}
