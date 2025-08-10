package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.enums.Categoryname;
import com.handalsali.handali.exception.MoreOneLessThreeSelectException;
import com.handalsali.handali.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/habits")
public class HabitController {
    private final HabitService habitService;
    private final BaseController baseController;

    public HabitController(HabitService habitService, BaseController baseController) {
        this.habitService = habitService;
        this.baseController = baseController;
    }


    /**
     * [습관추가]
     */
    @Operation(summary = "습관 추가", description = "사용자가 습관을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "습관 추가 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "message": "습관이 성공적으로 추가되었습니다."
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "습관 에러",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "하나 이상의 습관을 선택해주세요."
                      "습관은 최대 3개까지 선택할 수 있습니다."
                    }
                    """)))
    })
    @PostMapping
    public ResponseEntity<HabitDTO.AddHabitApiResponse> createUserHabit(@RequestHeader("Authorization") String accessToken,
                                                                        @RequestBody HabitDTO.AddHabitApiRequest request) {
        String token = baseController.extraToken(accessToken);
        if(request.getHabits() == null || request.getHabits().isEmpty()) {
            throw new MoreOneLessThreeSelectException("하나 이상의 습관을 선택해주세요.");
        }
        if(request.getHabits().size()>3){
            throw new MoreOneLessThreeSelectException("습관은 최대 3개까지 선택할 수 있습니다.");
        }

        habitService.createUserHabit(token,request);

        HabitDTO.AddHabitApiResponse response=new HabitDTO.AddHabitApiResponse("습관이 성공적으로 추가되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * [이번달 습관으로 지정]
     */
    @Operation(summary = "이번달 습관 지정", description = "사용자가 처음 추가한 습관을 바탕으로 이번 달 메인습관 선택")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처음 추가한 습관 바탕으로 습관 지정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "message": "습관이 이번달 습관으로 지정되었습니다."
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "습관 지정 에러",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "하나 이상의 습관을 선택해주세요.",
                      "습관은 최대 3개까지 선택할 수 있습니다."
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "습관 존재 에러",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "습관이 존재하지 않습니다."
                      "사용자가 해당 습관을 추가하지 않았습니다. : {details}"
                    }
                    """)))
    })
    @PostMapping("/set")
    public ResponseEntity<?> addHabitsForCurrentMonth(@RequestHeader("Authorization") String accessToken,
                                                      @RequestBody HabitDTO.AddHabitApiRequest request){
        String token = baseController.extraToken(accessToken);

        if(request.getHabits() == null || request.getHabits().isEmpty()) {
            throw new MoreOneLessThreeSelectException("하나 이상의 습관을 선택해주세요.");
        }
        if(request.getHabits().size()>3){
            throw new MoreOneLessThreeSelectException("습관은 최대 3개까지 선택할 수 있습니다.");
        }

        habitService.addHabitsForCurrentMonth(token,request);

        HabitDTO.AddHabitApiResponse response=new HabitDTO.AddHabitApiResponse("습관이 이번달 습관으로 지정되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * [지난달 습관 갱신]
     */
    @PatchMapping("/refresh-last-month")
    public ResponseEntity<String> refreshLastMonth(@RequestHeader("Authorization") String accessToken){
        String token = baseController.extraToken(accessToken);

        habitService.refreshLastMonthHabits(token);

        return ResponseEntity.status(HttpStatus.OK).body("이번달 습관이 지난달 습관으로 갱신되었습니다.");
    }


    /**[카테고리, 사용자에 따른 습관 조회]*/
    @GetMapping("/category-user")
    public ResponseEntity<HabitDTO.getHabitsApiResponse> getHabitsByUser(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String category) {

        String token = baseController.extraToken(accessToken);

        HabitDTO.getHabitsApiResponse response=habitService.getHabitsByUser(token,category);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**[카테고리, 개발자에 따른 습관 조회]*/
    @GetMapping("/category-dev")
    public ResponseEntity<HabitDTO.getHabitsApiResponse> getHabitsByDev(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String category){

        String token = baseController.extraToken(accessToken);

        HabitDTO.getHabitsApiResponse response=habitService.getHabitsByDev(token,category);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    /**
     * [카테고리,달에 따른 습관 조회]
     */
    @Operation(summary = "월별 습관 조회", description = "특정 카테고리와 월에 따른 습관 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처음 추가한 습관 바탕으로 습관 지정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                    "habits":[
                    {
                    "habit_id":"",
                    "detail":""
                    }
                    ],
                    "category":""
                    }
                    """)))
    })
    @GetMapping("/category-month")
    public ResponseEntity<Map<String, Object>> getHabitsByUserAndCategoryAndMonth(
            @RequestHeader("Authorization") @Parameter(description = "사용자 인증 토큰") String accessToken,
            @RequestParam @Parameter(description = "조회할 카테고리") Categoryname category,
            @RequestParam  @Parameter(description = "조회할 월") int month) {

        String token = baseController.extraToken(accessToken);

        Map<String, Object> habits = habitService.getHabitsByUserAndCategoryAndMonth(token,category,month);
        return ResponseEntity.status(HttpStatus.OK).body(habits);
    }
}
