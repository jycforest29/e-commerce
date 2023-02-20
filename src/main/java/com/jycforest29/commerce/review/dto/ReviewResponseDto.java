package com.jycforest29.commerce.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jycforest29.commerce.review.domain.entity.Review;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ReviewResponseDto {
    private String title;
    private String contents;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long itemId;
    private String username;
    public static ReviewResponseDto from(Review review){
        return ReviewResponseDto.builder()
                .title(review.getTitle())
                .contents(review.getContents())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .itemId(review.getItem().getId())
                .username(review.getAuthUser().getUsername())
                .build();
    }
}
