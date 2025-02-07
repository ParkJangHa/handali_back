package com.handalsali.handali.service;

import com.handalsali.handali.DTO.JobStatDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.repository.HandaliRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentService {
    @Autowired
    private HandaliRepository handaliRepository;

    //[아파트에 입주한 모든 한달이 조회]
    public List<JobStatDTO.JobResponse> getAllHandalis() {
        return handaliRepository.findAll() // 모든 한달이 조회
                .stream()
                .map(Handali::toJobResponse) // DTO 변환
                .collect(Collectors.toList());
    }
}
