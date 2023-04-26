package com.jpa.shop.api;

import com.jpa.shop.domain.Address;
import com.jpa.shop.domain.Order;
import com.jpa.shop.domain.OrderItem;
import com.jpa.shop.domain.OrderStatus;
import com.jpa.shop.query.OrderQueryDto;
import com.jpa.shop.repository.OrderQueryRepository;
import com.jpa.shop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    @GetMapping("/api/v1/orders")
    public List<OrderDto> orderV1(){
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    // 페치조인은 pagin 처리가 불가능하다 + jpql안에 있는 메서드로 가능
    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2(@RequestParam(value ="offset" , defaultValue = "0") int offset,
                                  @RequestParam(value ="limit" , defaultValue = "100") int limit
                                  ){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset , limit);
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }
    private final OrderQueryRepository orderQueryRepository;
    @GetMapping("/api/v3/orders")
    public List<OrderQueryDto> orderV3(){
        return orderQueryRepository.findOrderQueryDtos();
    }
    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;//상품 명
        private int orderPrice; //주문 가격
        private int count; //주문 수량
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
