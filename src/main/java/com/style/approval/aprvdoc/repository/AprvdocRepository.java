package com.style.approval.aprvdoc.repository;

import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AprvdocRepository extends JpaRepository<AprvdocEntity, Long> {
    Optional<AprvdocEntity> findByDocNo(String docNo);
    List<AprvdocEntity> findByRegUserOrderByRegDateDesc(UserEntity regUser);
    List<AprvdocEntity> findByRegUserAndStatusOrderByRegDateDesc(UserEntity regUser, String status);

    @Query(value = "select doc.* from Aprvdoc doc " +
            " inner join Aprvline line" +
            " on doc.doc_no = line.doc_no" +
            " and line.status=:status" +
            " and line.aprv_user_id= :aprvUserId", nativeQuery = true)
    List<AprvdocEntity> findInboxJQPL(@Param("aprvUserId") Long aprvUserId,
                                                    @Param("status") String status);

    @Query(value = "select doc.* from Aprvdoc doc " +
            " inner join Aprvline line" +
            " on doc.doc_no = line.doc_no" +
            " and line.aprv_user_id = :aprvUserId" +
            " where doc.status IN :status", nativeQuery = true)
    List<AprvdocEntity> findArchiveJQPL(@Param("aprvUserId") Long aprvUserId,
                                      @Param("status") List<String> status);

}
