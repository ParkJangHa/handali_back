package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.enums_multyKey.TypeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatRepository extends JpaRepository<Stat,Long> {
}
