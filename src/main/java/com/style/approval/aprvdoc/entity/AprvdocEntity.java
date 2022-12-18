package com.style.approval.aprvdoc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.style.approval.aprvline.entity.AprvlineEntity;
import com.style.approval.user.entity.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="aprvdoc")
public class AprvdocEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique=true)
    private String docNo;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String content;

    @Column(length = 20)
    private String category;

    @Column(length = 100)
    private String aprvOrder;

    @Column(length = 1)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_user_id")
    private UserEntity regUser;

    @Column
    private LocalDateTime regDate;

    @Column
    private LocalDateTime endDate;

}
