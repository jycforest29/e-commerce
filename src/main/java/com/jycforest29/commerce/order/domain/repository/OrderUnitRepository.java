package com.jycforest29.commerce.order.domain.repository;

import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderUnitRepository extends JpaRepository<OrderUnit, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from OrderUnit o where o.id in :orderUnitIdList")
    void deleteAllByOrderUnitIdList(@Param("orderUnitIdList") List<Long> orderUnitIdList);
}
