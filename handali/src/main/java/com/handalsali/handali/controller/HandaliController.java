package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.service.HandaliService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/handalis")
public class HandaliController {
    private BaseController baseController;
    private HandaliService handaliService;
//    private UserService userService;

    public HandaliController(BaseController baseController,HandaliService handaliService){
        this.baseController=baseController;
        this.handaliService=handaliService;
//        this.userService = userService;
    }

    /**
     * [한달이 생성]
     */
    @Operation(summary = "한달이 생성", description = "새로운 한달이를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "한달이 생성 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                    "handali_id":"",
                    "nickname":"",
                    "start_date":"",
                    "message":"한달이가 생성되었습니다."
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "한달이 생성 에러",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "error": "한달에 한 마리만 생성 가능합니다."
                    }
                    """)))
    })
    @PostMapping
    public ResponseEntity<HandaliDTO.CreateHandaliResponse> handaliCreate(@RequestHeader("Authorization") String accessToken,
                                                @RequestBody HandaliDTO.CreateHandaliRequest request){

        String token=baseController.extraToken(accessToken);
        Handali handali=handaliService.handaliCreate(token,request.getNickname());
        HandaliDTO.CreateHandaliResponse createHandaliResponse=new HandaliDTO.CreateHandaliResponse(handali.getHandaliId(),handali.getNickname(),handali.getStartDate(),"한달이가 생성되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(createHandaliResponse);
    }


    /**
     * [한달이 상태 조회]
     */
    @Operation(summary = "한달이 상태 조회", description = "현재 한달이의 상태 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "습관 기록 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                    "nickname":"",
                    "days_since_created":"",
                    "total_coin":"",
                    "image":""
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "한달이 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "error": "한달이가 존재하지 않습니다."
                    }
                    """)))
    })
    @GetMapping("/view")
    public ResponseEntity<HandaliDTO.HandaliStatusResponse> getHandaliStatus(
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);
        HandaliDTO.HandaliStatusResponse response = handaliService.getHandaliStatusByMonth(token);
        return ResponseEntity.ok(response);
    }

    /**
     * [스탯 조회]
     */
    @Operation(summary = "한달이 스탯 조회", description = "특정 한달이의 스탯 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "습관 기록 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                    "stat":[
                    {
                    "type_name":"",
                    "value":""
                    }
                    ]
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
    @GetMapping("/{handali_id}/stats")
    public ResponseEntity<HandaliDTO.StatResponse> getStatsByHandaliId(
            @PathVariable("handali_id") Long handaliId,
            @RequestHeader("Authorization") String accessToken) {

        // 토큰 처리
        String token = baseController.extraToken(accessToken);
        // 서비스 계층에서 스탯 데이터 가져오기
        HandaliDTO.StatResponse response = handaliService.getStatsByHandaliId(handaliId, token);

        return ResponseEntity.ok(response);
    }

    /**
     * [한달이 상태 변화]
     */
    @Operation(summary = "한달이 상태 변경", description = "현재 한달이의 상태 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 변경 성공"),
            @ApiResponse(responseCode = "401", description = "변경 실패")
    })
    @GetMapping("/change")
    public ResponseEntity<String> changeHandali(@RequestHeader("Authorization") String accessToken){
        String token = baseController.extraToken(accessToken);

        return ResponseEntity.ok(handaliService.changeHandali(token));
    }

    /**
     * [마지막 생성 한달이 조회]
     */
    @Operation(summary = "최근 한달이 조회", description = "마지막으로 생성된 한달이 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마지막 생성 한달이 조회 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                    "nickname":"",
                    "handali_id":"",
                    "start_date":"",
                    "job_name":"",
                    "salary":"",
                    "image":""
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "마지막 생성 한달이 조회 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "error": "최근 생성된 한달이를 찾을 수 없습니다."
                    }
                    """)))
    })
    @GetMapping("/recent")
    public ResponseEntity<HandaliDTO.RecentHandaliResponse> getRecentHandali(
            @RequestHeader("Authorization") String accessToken) {

        // 토큰 처리
        String token = baseController.extraToken(accessToken);
        HandaliDTO.RecentHandaliResponse response = handaliService.getRecentHandali(token);

        return ResponseEntity.ok(response);
    }

//    // (test) 🚀 강제 실행: 매달 1일 자동 실행을 지금 즉시 실행!
//    @PostMapping("/process-monthly")
//    public ResponseEntity<String> processMonthlyJobAndApartmentEntry() {
//        handaliService.processMonthlyJobAndApartmentEntry();
//        return ResponseEntity.ok("한달이 취업 + 아파트 입주가 강제로 실행되었습니다!");
//    }
}
