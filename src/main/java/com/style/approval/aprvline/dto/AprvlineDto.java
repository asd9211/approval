package com.style.approval.aprvline.dto;

import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.aprvline.entity.AprvlineEntity;
import com.style.approval.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AprvlineDto {

    private AprvlineDto(){}

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Request {
        private Long id;
        private String docNo;

        private UserEntity user;

        private AprvdocEntity aprvdoc;
        private Long seqNo;
        private String comment;
        private String username;
        private String status;


        public AprvlineEntity toEntity(){
            return AprvlineEntity.builder()
                    .docNo(docNo)
                    .seqNo(seqNo)
                    .status(status)
                    .aprvdoc(aprvdoc)
                    .aprvUser(user)
                    .regDate(LocalDateTime.now())
                    .comment(comment)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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

        public Response(AprvlineEntity aprvline){
            this.id = aprvline.getId();
            this.docNo = aprvline.getDocNo();
            this.seqNo = aprvline.getSeqNo();
            this.comment = aprvline.getComment();
            this.username = aprvline.getAprvUser().getUsername();
            this.status = aprvline.getStatus();
            this.regDate = aprvline.getRegDate();
            this.aprvDate = aprvline.getAprvDate();

        }
    }

}
