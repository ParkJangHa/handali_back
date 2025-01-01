package com.handalsali.handali.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;


public class UserDTO {
    @Data
    public static class SignUpRequest{
        @NotBlank(message = "이름을 입력해주세요.")
        private String name;
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
        @NotBlank(message = "전화번호를 입력해주세요.")
        private String phone;
        @NotNull(message ="생년월일을 입력해주세요.")
        private Date birthday;
    }

    @Data
    @AllArgsConstructor
    public static class SignUpResponse{
        private String name;
    }
}
