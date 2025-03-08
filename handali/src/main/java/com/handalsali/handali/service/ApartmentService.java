package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApartmentService {
    private final HandaliRepository handaliRepository;
    private final UserService userService;
    private final HandaliService handaliService;

    public ApartmentService(HandaliRepository handaliRepository, UserService userService, HandaliService handaliService) {
        this.handaliRepository = handaliRepository;
        this.userService = userService;
        this.handaliService = handaliService;
    }

    /**[아파트 내 모든 한달이 조회]*/
    public List<Map<String, Object>> getAllHandalisInApartments(String token) {
        User user = userService.tokenToUser(token);
        List<Handali> handalis = handaliRepository.findAllByUser(user);

        if (handalis.isEmpty()) {throw new HandaliNotFoundException("한달이가 존재하지 않습니다.");
        }

        return handalis.stream()
                .map(handali -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("apart_id", handali.getApart().getApartId());
                    response.put("floor", handali.getApart().getFloor());
                    response.put("nickname", handali.getNickname());
                    response.put("start_date", handali.getStartDate());
                    response.put("job_name", handali.getJob().getName());
                    response.put("week_salary", handali.getJob().getWeekSalary());
                    response.put("image", handali.getImage());
                    return response;
                })
                .sorted(Comparator.comparing(map -> (Integer) map.get("apart_id"))) // apart_id 기준 오름차순 정렬
                .collect(Collectors.toList());
    }
}
