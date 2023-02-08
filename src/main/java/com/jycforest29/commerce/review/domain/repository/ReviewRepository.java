package com.jycforest29.commerce.review.domain.repository;

import com.jycforest29.commerce.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
// dao VS repository : dao는 sql 수준에서 db와 맵핑, repository는 객체 수준에서 db와 맵핑.
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r where r.item.id = :itemId")
    List<Review> findAllByItemId(Long itemId);

    @Query("select r from Review r where r.authUser.id = :authUserId and timestampdiff(hour, r.createdAt, :now) <= 50")
    List<Review> findAllByAuthUserAndCreatedWithin50Hours(Long authUserId, LocalDateTime now);

}
