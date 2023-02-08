package com.jycforest29.commerce.review.domain.dto;

import com.jycforest29.commerce.user.domain.entity.AuthUser;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AddReviewRequestDTO {

    @Size(min = 10, max = 255, message = "제목은 10~255 글자여야 합니다.")
    private String title;
    @Size(min = 10, max = 255, message = "내용은 10~255 글자여야 합니다.")
    private String contents;

}
