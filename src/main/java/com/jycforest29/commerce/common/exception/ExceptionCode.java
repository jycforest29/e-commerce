package com.jycforest29.commerce.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// !Exception 분리 생각
@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    UNAUTHORIZED(401, "권한이 없음"),
    ENTITY_NOT_FOUND(404, "엔티티가 존재하지 않음"),
    REVIEW_LIKE_DUPLICATED(600, "이미 좋아요를 누른 리뷰임"),
    REVIEW_LIKE_NOT_EXISTS(601, "좋아요를 누르지 않은 리뷰는 좋아요 취소 불가함"),
    ITEM_OVER_LIMIT(800, "아이템의 수량 초과"),
    NOT_DONE_BY_AUTHUSER(900, "내가 수행한 동작이 아님"),
    CANNOT_LIKE_DONE_BY_AUTHUSER(900, "내가 작성한 리뷰에 대해서 좋아요 불가함"),
    USERNAME_DUPLICATED(910, "USERNAME이 이미 존재함"),
    REFRESH_TOKEN_EXPIRED(915, "리프레시 토큰이 만료됨");

    private final int status;
    private final String msg;
}
