package com.handalsali.handali.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.handalsali.handali.DTO.JobStatDTO;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {
    private final JobService jobService;
    private final BaseController baseController;

    public JobController(JobService jobService, BaseController baseController) {
        this.jobService = jobService;
        this.baseController = baseController;
    }

    /**[취업]*/
    @PostMapping("/handalis/{handali_id}/job")
    public ResponseEntity<JobStatDTO.JobResponse> assignJob(@RequestHeader("Authorization") String accessToken,
                                                @PathVariable("handali_id") Long handaliId) {
        String token = baseController.extraToken(accessToken);

        return ResponseEntity.status(HttpStatus.OK).body(jobService.assignBestJobToHandali(token,handaliId));
    }
}
