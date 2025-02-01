package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Apartment;
import com.handalsali.handali.enums_multyKey.ApartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, ApartId> {
    int countByApartId_ApartId(int apartId);
    Apartment findTopByOrderByApartId_ApartIdDesc();
}