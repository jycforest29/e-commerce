package com.jycforest29.commerce.review.domain.repository;

import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewLikeUnitRepository extends JpaRepository<ReviewLikeUnit, Long> {
    List<ReviewLikeUnit> findAllByAuthUser(AuthUser authUser);
    Optional<ReviewLikeUnit> findByReviewAndAuthUser(Review review, AuthUser authUser);

    // deleteAll() : select 쿼리 한번, 삭제 쿼리 n번
    // deleteAllInBatch() : 한방 쿼리를 통해서 삭제함(엔티티 이름을 기반으로 해당하는 테이블을 찾아서 한방에 데이터 삭제함)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ReviewLikeUnit r where r.review.id = :review_id")
    void deleteAllByReviewId(@Param("review_id") Long review_id);

    List<ReviewLikeUnit> findAllByReview(Review review);
}
