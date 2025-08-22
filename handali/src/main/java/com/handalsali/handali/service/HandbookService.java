package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandbookDTO;
import com.handalsali.handali.domain.Handbook;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHandbook;
import com.handalsali.handali.repository.HandbookRepository;
import com.handalsali.handali.repository.UserHandbookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HandbookService {
    private final HandbookRepository handbookRepository;
    private final UserHandbookRepository userHandbookRepository;

    public HandbookService(HandbookRepository handbookRepository, UserHandbookRepository userHandbookRepository) {
        this.handbookRepository = handbookRepository;
        this.userHandbookRepository = userHandbookRepository;
    }

    /**
     * 도감 추가
     */
    public void addHandbook(User user, String code){
        //1. 코드가 도감 테이블에 존재하는지 확인
        Handbook handbook = handbookRepository.findByCode(code);

        //2. 존재할 경우, 유저-도감 테이블에 추가
        if(handbook != null){
            boolean exists = userHandbookRepository.existsByUserAndHandbook(user, handbook);
            if(!exists){
                UserHandbook userHandbook = new UserHandbook(user, handbook);
                //3. 데이터베이스에 저장
                userHandbookRepository.save(userHandbook);
            }
        }
    }

    /**
     * [도감 조회]
     */
    public HandbookDTO.HandbookApiResponse getUserHandbook(User user) {
        List<HandbookDTO.HandbookResponse> responses = userHandbookRepository.findAllByUser(user)
                .stream()
                .map(uh -> new HandbookDTO.HandbookResponse(
                        uh.getHandbook().getCode(),
                        uh.getCreatedAt()
                ))
                .toList();

        return new HandbookDTO.HandbookApiResponse(responses);
    }

}
