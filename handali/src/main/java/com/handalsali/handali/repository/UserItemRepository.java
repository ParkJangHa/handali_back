package com.handalsali.handali.repository;

import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserItem;
import com.handalsali.handali.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    @Query("select ui from UserItem ui " +
            "where ui.user=:user and " +
            "ui.store.itemType=:itemType and " +
            "ui.isAvailable=true")
    Optional<UserItem> findByUserAndItemType(@Param("user") User user, @Param("itemType") ItemType itemType);

    @Query("select ui from UserItem ui " +
            "where ui.user=:user and " +
            "ui.store.itemType=:itemType and " +
            "ui.store.name=:name")
    Optional<UserItem> findByUserAndItemTypeAndName(@Param("user") User user, @Param("itemType") ItemType itemType,@Param("name")String name);
}
