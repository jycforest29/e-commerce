package com.jycforest29.commerce.review.controller.dto;

import lombok.*;

import javax.validation.constraints.Size;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AddReviewRequestDto {
    @Size(min = 10, max = 255, message = "제목은 10~255 글자여야 합니다.")
    private String title;
    @Size(min = 10, max = 255, message = "내용은 10~255 글자여야 합니다.")
    private String contents;
}
