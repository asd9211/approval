package com.style.approval.aprvline.dto;

import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.aprvline.entity.AprvlineEntity;
import com.style.approval.enums.AprvStatus;
import com.style.approval.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AprvlineDto {

    private AprvlineDto() {
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Request {
        private Long id;
        private String docNo;
        private Long seqNo;
        private String comment;
        private String username;
        private AprvStatus status;


    }

    @Builder
    @AllArgsConstructor
    @Data
    public static class Response {
        private Long id;
        private String docNo;
        private Long seqNo;
        private String comment;
        private String username;
        private String status;
        private LocalDateTime regDate;
        private LocalDateTime aprvDate;

        public Response(AprvlineEntity aprvlineEntity) {
            this.docNo = aprvlineEntity.getDocNo();
            this.seqNo = aprvlineEntity.getSeqNo();
            this.comment = aprvlineEntity.getComment();
            this.username = aprvlineEntity.getAprvUser().getUsername();
            this.status = aprvlineEntity.getStatus().getCode();
            this.regDate = aprvlineEntity.getRegDate();
            this.aprvDate = aprvlineEntity.getAprvDate();
        }

    }

}
