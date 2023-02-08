package com.jycforest29.commerce.review.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jycforest29.commerce.review.domain.entity.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HomeResponseDto {
    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long itemId;

    private Long authUserId;

    public static HomeResponseDto from(Review review){
        return new HomeResponseDtoBuilder()
                .title(review.getTitle())
                .content(review.getContents())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .itemId(review.getItem().getId())
                .authUserId(review.getAuthUser().getId())
                .build();
    }

}
