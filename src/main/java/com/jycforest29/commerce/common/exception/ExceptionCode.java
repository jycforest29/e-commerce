package com.jycforest29.commerce.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// !Exception 분리 생각
@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    USER_NOT_FOUND(401, "엔티티가 존재하지 않음"),
    ENTITY_NOT_FOUND(404, "엔티티가 존재하지 않음"),
    REVIEW_LIKE_DUPLICATED(600, "이미 좋아요를 누른 리뷰임"),
    ITEM_OVER_LIMIT(800, "아이템의 수량 초과"),
    NOT_DONE_BY_AUTHUSER(900, "내가 수행한 동작이 아님");

    private final int status;
    private final String msg;
}
