package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Getter
@Table(name="user")
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

    public User(String email, String name, String password, String phone, LocalDate birthday){
        this.email=email;
        this.name=name;
        setPassword(password);
        this.phone=phone;
        this.birthday=birthday;
    }


    //비밀번호 암호화
    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    public void setPassword(String password) {
        this.password = passwordEncoder.encode(password);
    }
    public boolean checkPassword(String rawPassword){
        return passwordEncoder.matches(rawPassword,this.password);
    }

    public void setTotalCoin(int totalCoin) {
        this.total_coin = totalCoin;
    }
}
