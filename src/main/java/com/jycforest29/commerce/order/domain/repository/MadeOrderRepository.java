package com.jycforest29.commerce.order.domain.repository;

import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MadeOrderRepository extends JpaRepository<MadeOrder, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from MadeOrder m where m.id in :aLong")
    void deleteById(@Param("aLong") List<Long> aLong);

    List<MadeOrder> findAllByAuthUserOrderByCreatedAtDesc(@Param("authUser") AuthUser authUser);
}
