package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.*;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.ApartRepository;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.JobRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ApartmentService {
    private final HandaliRepository handaliRepository;
    private final UserService userService;
    private final ApartRepository apartRepository;
    private final JobRepository jobRepository;
    private final HandbookService handbookService;

    public ApartmentService(HandaliRepository handaliRepository, UserService userService, ApartRepository apartRepository, JobRepository jobRepository, HandbookService handbookService) {
        this.handaliRepository = handaliRepository;
        this.userService = userService;
        this.apartRepository = apartRepository;
        this.jobRepository = jobRepository;
        this.handbookService = handbookService;
    }

    /** 한달이의 아파트 배정 **/
    // 생성 월에 따라 층 결정, 연도가 바뀌면 새로운 아파트에 입주
    public Apart assignApartmentToHandali(Handali handali) {
        int year = handali.getStartDate().getYear();  // 생성 연도
        int month = handali.getStartDate().getMonthValue();

        Apart newApartment = new Apart(
                handali.getUser(),
                handali,
                handali.getNickname(),
                month,  // 층수는 생성 월
                year  // 아파트 ID는 생성 연도
        );

        apartRepository.save(newApartment);

        return newApartment;
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
                    response.put("background_img", handali.getBackground());
                    response.put("sofa_img", handali.getSofa());
                    response.put("floor_img", handali.getFloor());
                    response.put("wall_img", handali.getWall());
                    return response;
                })
                .sorted(Comparator.comparing(map -> (Integer) map.get("apart_id"))) // apart_id 기준 오름차순 정렬
                .collect(Collectors.toList());
    }

    @Transactional
    public void createHandaliAndApartment(User user, int year, int month) {
        Optional<Apart> findApart = apartRepository.findByApartIdAndFloor(year, month);
        if (findApart.isPresent()) {
            throw new HandaliNotFoundException("이미 해당 위치(" + year + "년 " + month + "월)에 아파트가 존재합니다.");
        }

        // 1. 필요한 객체들 생성
        LocalDate date = LocalDate.of(year, month, 1);
        String name = month + "월이";

        // 2. DB에서 Job을 랜덤으로 조회
        long randomJobId = ThreadLocalRandom.current().nextInt(1, 31);
        Job foundJob = jobRepository.findById(randomJobId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + randomJobId + "에 해당하는 Job을 찾을 수 없습니다."));

        // 3. Handali와 Apart 객체 생성

        //한달이 이미지 랜덤 설정
        int randomNumber1 = ThreadLocalRandom.current().nextInt(0, 6);
        int randomNumber2 = ThreadLocalRandom.current().nextInt(0, 6);
        int randomNumber3 = ThreadLocalRandom.current().nextInt(0, 6);
        String fileName = String.format("image_%d_%d_%d.png",
                randomNumber1,
                randomNumber2,
                randomNumber3);

        Handali handali = new Handali(name, date, user);
        handali.setImage(fileName);

        Apart apart = new Apart(user, handali, name, month, year);

        // 4. 객체 간의 연관관계 설정 (메모리상에서 연결)
        handali.setApart(apart);
        handali.setJob(foundJob); // Optional이 아닌 실제 Job 객체를 전달

        // 5. Repository에 저장
        // foundJob은 이미 DB에 있는 데이터이므로 다시 save할 필요가 없습니다.
        handaliRepository.save(handali);
        apartRepository.save(apart);

        //6.도감에 추가
        for(int i = 0; i<=randomNumber1; i++){
            for(int j = 0; j<=randomNumber2; j++){
                for(int k = 0; k<=randomNumber3; k++){
                    handbookService.addHandbook(user,String.format("image_%d_%d_%d.png", i, j, k));
                }
            }
        }
    }
}
