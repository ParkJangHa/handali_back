package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Store;
import com.handalsali.handali.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
    List<Store> findByItemType(ItemType itemType);

    Optional<Store> findByItemTypeAndName(ItemType itemType, String name);
}
