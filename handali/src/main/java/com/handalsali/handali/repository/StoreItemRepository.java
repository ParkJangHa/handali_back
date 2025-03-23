package com.handalsali.handali.repository;

import com.handalsali.handali.domain.StoreItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreItemRepository extends JpaRepository<StoreItem,Long> {
    List<StoreItem> findByCategory(String category);
    Optional<StoreItem> findByCategoryAndName(String category, String name);
}
