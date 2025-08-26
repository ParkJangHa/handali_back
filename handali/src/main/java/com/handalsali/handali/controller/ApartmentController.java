package com.handalsali.handali.controller;

import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.ApartRepository;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.JobRepository;
import com.handalsali.handali.service.ApartmentService;
import com.handalsali.handali.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final BaseController baseController;
    private final UserService userService;


    public ApartmentController(ApartmentService apartmentService, BaseController baseController, UserService userService) {
        this.apartmentService = apartmentService;
        this.baseController = baseController;
        this.userService = userService;
    }

    //[아파트 내 모든 한달이 조회]
    @Operation(summary = "아파트 내 모든 한달이 조회", description = "아파트에 입주한 모든 한달이 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아파트 입주 모든 한달이 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                    "message": "",
                    "job_name": "",
                    "apart_id": "",
                    "nickname": "",
                    "week_salary": "",
                    "floor": "",
                    "start_date": ""
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "아파트 입주 한달이가 없을 경우",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "error": "한달이가 존재하지 않습니다."
                    }
                    """)))
    })
    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> getAllHandalisInApartments(
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);
        List<Map<String,Object>> response=apartmentService.getAllHandalisInApartments(token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * [개발용] 특정 년/월에 한달이 삽입
     */
    @PostMapping("/add-handali")
    public ResponseEntity<String> addHandalisInApartments(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam int year,
            @RequestParam int month
    ){
        String token = baseController.extraToken(accessToken);
        User user = userService.tokenToUser(token);

        apartmentService.createHandaliAndApartment(user, year, month);

        return ResponseEntity.status(HttpStatus.CREATED).body("한달이가 아파트에 추가되었습니다.");
    }
}
