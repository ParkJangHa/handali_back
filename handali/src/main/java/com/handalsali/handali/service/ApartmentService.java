package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.repository.HandaliRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentService {
    private final HandaliRepository handaliRepository;

    public ApartmentService(HandaliRepository handaliRepository) {
        this.handaliRepository = handaliRepository;
    }

    //[아파트에 입주한 모든 한달이 조회]
    public List<HandaliDTO.HandaliInApartmentResponse> getAllHandalisInApartments() {  // ✅ 반환 타입 수정
        return handaliRepository.findAllHandalisInApartments()
                .stream()
                .map(Handali::toApartmentResponse) // DTO 변환 방식 수정
                .collect(Collectors.toList());
    }
}
