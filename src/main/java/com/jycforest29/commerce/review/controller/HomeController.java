package com.jycforest29.commerce.review.controller;

import com.jycforest29.commerce.common.aop.LoginAuthUser;
import com.jycforest29.commerce.review.dto.ReviewResponseDto;
import com.jycforest29.commerce.review.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class HomeController {
    private final HomeService homeService;

    @GetMapping(value = "/home")
    public ResponseEntity<List<ReviewResponseDto>> getHomeReviewList(@LoginAuthUser Long authUserId){
        return ResponseEntity.status(HttpStatus.OK).body(homeService.getHomeReviewList(authUserId));
    }
}
