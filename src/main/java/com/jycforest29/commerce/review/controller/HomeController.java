package com.jycforest29.commerce.review.controller;

import com.jycforest29.commerce.common.aop.LoginAuthUser;
import com.jycforest29.commerce.review.domain.dto.HomeResponseDto;
import com.jycforest29.commerce.review.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/home") // consumes, produces를 사용해 요청, 응답시 데이터 포맷 지정 가능
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<List<HomeResponseDto>> getHomeReviewList(@LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(homeService.getHomeReviewList(authUserId));
    }
}
