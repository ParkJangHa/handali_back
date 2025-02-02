package com.handalsali.handali.controller;

import org.springframework.stereotype.Controller;

@Controller
public class BaseController {
    public String extraToken(String accessToken){
        return accessToken.replace("Bearer ", "").trim();
    }

}

//자동 배포 테스트 마지막