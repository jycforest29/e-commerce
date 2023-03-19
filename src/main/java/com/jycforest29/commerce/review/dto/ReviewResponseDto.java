package com.jycforest29.commerce.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jycforest29.commerce.review.domain.entity.Review;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private String title;
    private String contents;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul") // respones 가능
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;
    private String name;
    private String username;
    public static ReviewResponseDto from(Review review){
        return ReviewResponseDto.builder()
                .title(review.getTitle())
                .contents(review.getContents())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .name(review.getItem().getName())
                .username(review.getAuthUser().getUsername())
                .build();
    }
}
