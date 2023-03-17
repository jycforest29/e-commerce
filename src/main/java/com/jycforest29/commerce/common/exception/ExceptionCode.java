package com.jycforest29.commerce.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    // 400번대 : http status code 그대로 사용
    UNAUTHORIZED(401, "권한이 없음"),
    REFRESH_TOKEN_EXPIRED(403, "리프레시 토큰이 만료됨"), // 사용자가 리소스에 대한 권한을 갖고있지 않음
    ENTITY_NOT_FOUND(404, "엔티티가 존재하지 않음"),
    ITEM_OVER_LIMIT(416, "아이템의 수량 초과"), // 처리할 수 없는 요청 범위
    // 500번대 : 좋아요
    REVIEW_LIKE_DUPLICATED(500, "이미 좋아요를 누른 리뷰임"),
    REVIEW_LIKE_NOT_EXISTS(501, "좋아요를 누르지 않은 리뷰는 좋아요 취소 불가함"),
    CANNOT_LIKE_DONE_BY_AUTHUSER(502, "내가 작성한 리뷰에 대해서 좋아요 불가함"),
    // 600번대 : 회원 관련
    NOT_DONE_BY_AUTHUSER(600, "내가 수행한 동작이 아님"),
    USERNAME_DUPLICATED(601, "USERNAME이 이미 존재함");

    private final int status;
    private final String msg;
}
