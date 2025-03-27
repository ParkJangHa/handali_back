package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor
public class UserStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UserStoreId;

    @ManyToOne
    @JoinColumn(name="user_id",
            foreignKey = @ForeignKey (foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE",
                    name="fk_store_user"))
    private User user;

    @ManyToOne
    @JoinColumn(name="store_id",
            foreignKey = @ForeignKey (foreignKeyDefinition = "FOREIGN KEY (store_id) REFERENCES store(store_id) ON DELETE CASCADE ON UPDATE CASCADE",
                    name="fk_store_store"))
    private Store store;

    public UserStore(User user, Store store) {
        this.user = user;
        this.store = store;
    }
}
