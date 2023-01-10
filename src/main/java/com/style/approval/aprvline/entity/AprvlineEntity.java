package com.style.approval.aprvline.entity;

import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.enums.AprvStatus;
import com.style.approval.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="aprvline")
public class AprvlineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String docNo;

    @Column(length = 2)
    private Long seqNo;

    @Column(length = 1)
    private String status;

    @Column(length = 200)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprvdoc_id")
    private AprvdocEntity aprvdoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprv_user_id")
    private UserEntity aprvUser;

    @Column
    private LocalDateTime regDate;

    @Column
    private LocalDateTime aprvDate;

    public void setStatus(AprvStatus status){
        this.status = status.getCode();
    }

    public AprvStatus getStatus(){
        return AprvStatus.findByAprvStatusCode(status);
    }
}
