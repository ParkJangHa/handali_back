package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.RecordDTO;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.RecordRepository;
import com.handalsali.handali.service.RecordService;
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

@RestController
public class RecordController {
    private final UserService userService;
    private final RecordRepository recordRepository;
    private RecordService recordService;
    private BaseController baseController;
    public RecordController(RecordService recordService, BaseController baseController, UserService userService, RecordRepository recordRepository){
        this.recordService=recordService;
        this.baseController = baseController;
        this.userService = userService;
        this.recordRepository = recordRepository;
    }

    /**
     * [습관 기록 및 스탯 업데이트]
     */
    @Operation(summary = "습관 기록 및 스탯 업데이트", description = "습관을 기록하고 그에 따른 스탯을 업데이트 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "습관 기록 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                    "record_id":"",
                    "습관이 성공적으로 기록되었습니다.",
                    "appearance_change:true"
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "습관 중복 에러",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "error": "습관 '{detail}은(는) {date}에 이미 기록 되었습니다."
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "습관을 못찾을 경우",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "error": "습관을 찾을 수 없습니다."
                    }
                    """)))
    })
    @PostMapping("/habits/record")
    public ResponseEntity<RecordDTO.RecordTodayHabitResponse> recordTodayHabit(@RequestHeader("Authorization") String accessToken,
                                                                               @RequestBody RecordDTO.RecordTodayHabitRequest request){
        String token=baseController.extraToken(accessToken);

        RecordDTO.RecordTodayHabitResponse response=recordService.recordTodayHabit(
                token, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/habits/summary")
    public ResponseEntity<RecordDTO.RecordSummaryResponse> recordSummary(@RequestHeader("Authorization") String accessToken){
        String token=baseController.extraToken(accessToken);
        RecordDTO.RecordSummaryResponse response=recordService.recordSummary(token);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * [개발용] 당일의 기록 초기화
     */
    @DeleteMapping("/habits/record-delete")
    public ResponseEntity<String> deleteRecordForToday(@RequestHeader("Authorization") String accessToken){
        String token=baseController.extraToken(accessToken);
        User user = userService.tokenToUser(token);
        recordRepository.deleteTodayRecord(LocalDate.now());
        return ResponseEntity.status(HttpStatus.OK).body("오늘 기록한 습관이 모두 제거되었습니다.");
    }
}
