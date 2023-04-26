package com.jpa.shop.repository;

import com.jpa.shop.domain.Order;
import com.jpa.shop.query.OrderItemQueryDto;
import com.jpa.shop.query.OrderQueryDto;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
// 커멘드 -> 명령 : 추가, 수정, 삭제 executeUpdate()
// 쿼리 -> 조회 :select : executeQuery() : 까다롭다
@Repository
public class OrderRepository {

    private final EntityManager em;

    public OrderRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }
    // 순수 jpql 방식 복잡한 쿼리문 실행  : sql 쿼리문 x : JPA 내부에서만 사용(자바만 인식할수있는 쿼리 )
    public List<Order> findAllByString(OrderSearch orderSearch) {

            String jpql = "select o from Order o join o.member m";
            boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        // select o from Order o join o.member m where o.status = :status

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        // select o from Order o join o.member m where o.status = :status and m.name like :name

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();  // new CriteriaBuilde();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }
    // fetch k
    public List<Order> findAllWithMemberDelivery() {
        List<Order> list = em.createQuery(
                "select o from Order o " +
                "join fetch o.member " +
                " join fetch o.delivery d" , Order.class).getResultList();

        return list;
    }

    public List<Order> findAllWithItem() {
        return em.createQuery(
                        "select distinct o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d" +
                                " join fetch o.orderItems oi" +
                                " join fetch oi.item i", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        // createQuery @Entity , @Embeddable 바로 클래스 찾아서 사용가능하다

        List<Order> list = em.createQuery(
                "select o from Order o " +
                        "join fetch o.member " +
                        " join fetch o.delivery d" , Order.class)
                .setFirstResult(offset).setMaxResults(limit).getResultList();
        return list;
    }


}

