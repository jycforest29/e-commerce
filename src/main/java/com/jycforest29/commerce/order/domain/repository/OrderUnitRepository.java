package com.jycforest29.commerce.order.domain.repository;

import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface OrderUnitRepository extends JpaRepository<OrderUnit, Long> {
    // 다른 엔티티와의 연관 관계를 고려할 필요 없음
    // 쿼리의 성능을 높이려 jpql을 사용해 벌크 연산으로 작성함
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from OrderUnit o where o.id in :orderUnitIdList")
    void deleteAllByOrderUnitIdList(List<Long> orderUnitIdList);
}
