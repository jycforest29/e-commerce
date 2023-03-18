package com.jycforest29.commerce.review.domain.repository;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.domain.entity.Review;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
// dao VS repository : dao는 sql 수준에서 db와 맵핑, repository는 객체 수준에서 db와 맵핑.
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Cacheable(value = "review", key = "#reviewId", cacheManager = "ehCacheManager")
    @Override
    Optional<Review> findById(@Param("id") Long reviewId);

    @Cacheable(value = "reviewListByItem", key = "#item.id", cacheManager = "ehCacheManager")
    List<Review> findAllByItem(@Param("item") Item item);

    // @Modifying 은 @Query를 통해 작성된 insert, update, delete (select 제외) 쿼리에서 사용됨
    // 기본적으로 제공하는 쿼리에는 적용되지 않음
    // 주로 벌크 연산과 같이 이용되며 jpa entity life-cycle을 무시하고 쿼리가 실행되기에 영속성 컨텍스트 관리 주의 필요
    @Modifying(clearAutomatically = true, flushAutomatically = true) // 벌크 연산 전후 영속성 컨텍스트 clear 해줌 -> 동기화
    @Query("select r from Review r where r.authUser.id = :authUserId and timestampdiff(hour, r.createdAt, :now) <= 72")
    List<Review> findAllByAuthUserIdAndCreatedWithin72Hours(Long authUserId, LocalDateTime now);
}
