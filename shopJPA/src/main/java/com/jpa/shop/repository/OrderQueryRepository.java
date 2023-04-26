package com.jpa.shop.repository;

import com.jpa.shop.query.OrderItemQueryDto;
import com.jpa.shop.query.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    // order 리스트안에 있는 list는 값을 가져올수 없음
    public List<OrderQueryDto> findOrderQueryDtos() {
        //루트 조회(toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();
        //루프를 돌면서 컬렉션 추가(추가 쿼리 실행) : list를 우리가 못 넣주기 때문에
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new com.jpa.shop.query.OrderItemQueryDto(" +
                                "oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = : orderId",
                        OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        List<OrderQueryDto> list = em.createQuery(
                        "select new com.jpa.shop.query.OrderQueryDto(" +
                                "o.id, m.name, o.orderDate,o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
        return list;
    }

}
