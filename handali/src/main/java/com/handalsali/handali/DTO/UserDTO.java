package com.handalsali.handali.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;


public class UserDTO {
    @Data
    public static class SignUpRequest{
        private String name;

        private String email;
        private String password;
        private String phone;
        private Date birthday;
    }

    @Data
    @AllArgsConstructor
    public static class SignUpResponse{
        private long userId;
        private String name;
    }
}
