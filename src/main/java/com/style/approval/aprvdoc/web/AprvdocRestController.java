package com.style.approval.aprvdoc.web;

import com.style.approval.aprvdoc.dto.AprvdocDto;
import com.style.approval.aprvdoc.service.AprvdocService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/aprvdoc")
public class AprvdocRestController {

    private final AprvdocService aprvdocService;

    @GetMapping
    public List<AprvdocDto.Response> aprvdocList() {
        return aprvdocService.findAll();
    }

    @GetMapping("/username")
    public List<AprvdocDto.Response> aprvdocListByUsername(AprvdocDto.Request request) {
        return aprvdocService.findByUsername(request);
    }

    @GetMapping("/docno")
    public AprvdocDto.Response aprvdocListByDocNo(AprvdocDto.Request request) {
        return aprvdocService.findByDocNo(request);
    }

    @GetMapping("/inbox")
    public List<AprvdocDto.Response> inBoxAprvdocList(AprvdocDto.Request request) {
        return aprvdocService.findInbox(request);
    }

    @GetMapping("/outbox")
    public List<AprvdocDto.Response> outBoxAprvdocList(AprvdocDto.Request request) {
        return aprvdocService.findOutbox(request);
    }

    @GetMapping("/archive")
    public List<AprvdocDto.Response> archiveAprvdocList(AprvdocDto.Request request) {
        return aprvdocService.findArchive(request);
    }

    @PostMapping
    public boolean aprvdocAdd(@RequestBody AprvdocDto.Request request) {
        return aprvdocService.addAprvdoc(request);
    }

}
