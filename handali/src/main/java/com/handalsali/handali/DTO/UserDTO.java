package com.handalsali.handali.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDate;


public class UserDTO {
    @Data
    public static class SignUpRequest{
        @Schema(description = "가입하려는 유저 실제 이름", example = "minsu")
        @NotBlank(message = "이름을 입력해주세요.")
        private String name;
        @Schema(description = "가입하려는 유저의 이메일", example = "abc@gmail.com")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
        @Schema(description = "가입하려는 사용자의 비밀번호", example = "password123")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
        @Schema(description = "가입하려는 사용자의 전화번호", example = "01012345678")
        @NotBlank(message = "전화번호를 입력해주세요.")
        private String phone;
        @Schema(description = "가입하려는 사용자의 생년월일", example = "1990-01-01")
        @NotNull(message ="생년월일을 입력해주세요.")
        private LocalDate birthday;
    }

    @Data
    @AllArgsConstructor
    public static class SignUpResponse{
        private String name;
    }

    @Data
    public static class LogInRequest{
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(description = "사용자의 이메일", example = "example@email.com")
        private String email;

        @Schema(description = "사용자의 비밀번호", example = "password123")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    @Data
    public static class QuestAwardRequest{
        private int coin;
    }
}
