package com.style.approval.aprvline.web;

import com.style.approval.aprvline.dto.AprvlineDto;
import com.style.approval.aprvline.service.AprvlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/aprvline")
public class AprvlineRestController {

    private final AprvlineService aprvlineService;

    @GetMapping("/docno")
    public List<AprvlineDto.Response> aprvlineListByDocNo(AprvlineDto.Request request){
        return aprvlineService.findAllByDocNo(request);
    }

    @GetMapping("/username/docno")
    public AprvlineDto.Response aprvlineByDocNoAndUsername(AprvlineDto.Request request){
        return aprvlineService.findByDocNoAndAprvUser(request);
    }

    @PutMapping("/accept")
    public boolean aprvlineAccept(@RequestBody AprvlineDto.Request request) {
        return aprvlineService.acceptAprvline(request);
    }

    @PutMapping("/reject")
    public boolean aprvlineReject(@RequestBody AprvlineDto.Request request) {
        return aprvlineService.rejectAprvline(request);
    }
}
