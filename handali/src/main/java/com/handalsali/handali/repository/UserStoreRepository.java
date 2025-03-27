package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Store;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStoreRepository extends JpaRepository<UserStore, Long> {
    UserStore findByUserAndStore(User user, Store store);
}
