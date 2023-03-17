package com.jycforest29.commerce.review.dto;

import lombok.*;

import javax.validation.constraints.Size;

@EqualsAndHashCode
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AddReviewRequestDto {
    @Size(min = 10, max = 255, message = "제목은 10~255 글자여야 합니다.")
    private String title;
    @Size(min = 10, max = 255, message = "내용은 10~255 글자여야 합니다.")
    private String contents;
}
