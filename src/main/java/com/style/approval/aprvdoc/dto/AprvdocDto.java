package com.style.approval.aprvdoc.dto;

import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.user.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class AprvdocDto {

    private AprvdocDto(){}

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Request {
        private String docNo;
        private String title;
        private String content;
        private UserEntity user;
        private String category;
        private String aprvOrder;
        private String status;
        private String username;
        private List<AprvlineDto.Request> aprvlineList;
        private LocalDateTime regDate;
        private LocalDateTime endDate;

        public AprvdocEntity toEntity(){
            return AprvdocEntity.builder()
                    .docNo(docNo)
                    .title(getTitle())
                    .content(getContent())
                    .regUser(user)
                    .regDate(LocalDateTime.now())
                    .status(getStatus())
                    .category(getCategory())
                    .aprvOrder(getAprvOrder())
                    .build();
        }

    }

    @Builder
    @AllArgsConstructor
    @Setter
    @Getter
    public static class Response {
        private String docNo;
        private String title;
        private String content;
        private String category;
        private String aprvOrder;
        private String status;
        private String username;
        List<AprvlineDto.Response> aprvlineList;
        private LocalDateTime regDate;
        private LocalDateTime endDate;

        public Response(AprvdocEntity aprvdoc){
                    this.title = aprvdoc.getTitle();
                    this.content=aprvdoc.getContent();
                    this.username=aprvdoc.getRegUser().getUsername();
                    this.docNo=aprvdoc.getDocNo();
                    this.category=aprvdoc.getCategory();
                    this.aprvOrder=aprvdoc.getAprvOrder();
                    this.status=aprvdoc.getStatus();
                    this.regDate=aprvdoc.getRegDate();
                    this.endDate=aprvdoc.getEndDate();
        }

    }

}
