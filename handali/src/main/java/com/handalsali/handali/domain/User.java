package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name="user")
@SQLDelete(sql = "UPDATE user SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @ColumnDefault("0")
    @Setter
    private int total_coin;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private LocalDate birthday;

    private boolean isDeleted = false;

    public void delete() {
        this.isDeleted = true;
    }

    public User(String email, String name, String password, String phone, LocalDate birthday){
        this.email=email;
        this.name=name;
        this.password=password;
        this.phone=phone;
        this.birthday=birthday;
    }

    // spring security 에서 사용 가능 하도록 UserDetails로  변환
    public UserDetails toUserDetails() {
        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(password)
                .roles("USER")
                .build();
    }
}
