package com.style.approval.aprvdoc.dto;

import com.style.approval.aprvdoc.entity.AprvdocEntity;
import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.enums.DocStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AprvdocDto {

    private AprvdocDto() {
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Request {
        private String docNo;
        private String title;
        private String content;
        private String category;
        private String aprvOrder;
        private String status;
        private String username;
        private List<AprvlineDto.Request> aprvlineList;
        private LocalDateTime regDate;
        private LocalDateTime endDate;

    }

    @Builder
    @Data
    @AllArgsConstructor
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


        public Response(AprvdocEntity aprvdocEntity) {
            this.docNo = aprvdocEntity.getDocNo();
            this.title = aprvdocEntity.getTitle();
            this.content = aprvdocEntity.getContent();
            this.category = aprvdocEntity.getCategory();
            this.aprvOrder = aprvdocEntity.getAprvOrder();
            this.status = aprvdocEntity.getStatus();
            this.username = aprvdocEntity.getRegUser().getUsername();
            this.aprvlineList = ObjectUtils.isEmpty(aprvdocEntity.getAprvlines()) ? new ArrayList<>() : aprvdocEntity.getAprvlines().stream().map(AprvlineDto.Response::new).collect(Collectors.toList());
            this.regDate = aprvdocEntity.getRegDate();
            this.endDate = aprvdocEntity.getEndDate();
        }
    }

}
