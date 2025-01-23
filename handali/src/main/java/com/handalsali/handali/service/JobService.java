package com.handalsali.handali.service;

import com.handalsali.handali.domain.Job;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class JobService {
    public Job createJob(Job job) {
        //1. 직업 이름, 주급을 설정
        //2. 해당 한달이의 스탯값을 모두 가져옴
        //3. 스탯중 가장 큰 값을 선택하여
    }
}
