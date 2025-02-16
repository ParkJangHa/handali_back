package com.handalsali.handali.service;

import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.repository.HandaliStatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HandaliStatService {
    private final HandaliStatRepository handaliStatRepository;
    public HandaliStatService(HandaliStatRepository handaliStatRepository) {
        this.handaliStatRepository = handaliStatRepository;
    }
        public List<HandaliStat> findMaxStatByHandaliId(Long handaliId) {
        return handaliStatRepository.findMaxStatByHandaliId(handaliId);
    }
}
