package com.jpa.shop.api;

import com.jpa.shop.domain.Address;
import com.jpa.shop.domain.Order;
import com.jpa.shop.domain.OrderStatus;
import com.jpa.shop.repository.OrderRepository;
import com.jpa.shop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        // findAllByString -> EAGER : 모든 join 값을 다 출력한다
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order : all){
            // lazy getMember , getDelivery 실제 값을 가져온다
            order.getMember().getName(); // LAZY 강제 EAGER 변경
            order.getDelivery().getAddress();
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        // stream map을 사용해서 바꿔치기
        List<SimpleOrderDto> result = orders.stream().map(order-> new SimpleOrderDto(order)).collect(Collectors.toList());
         return  result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream().map(order-> new SimpleOrderDto(order))
                .collect(Collectors.toList());

        return  result;
    }



    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address; //value object
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // select * member => 이때 lazy에 있는 프록시 객체 초기화 됨
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();// select * delivery
        }
    }


}
