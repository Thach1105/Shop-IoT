package com.thachnn.ShopIoT.api;
import com.thachnn.ShopIoT.dto.request.ChangStatusRequest;
import com.thachnn.ShopIoT.dto.request.OrderRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.OrderDetailResponse;
import com.thachnn.ShopIoT.dto.response.OrderResponse;
import com.thachnn.ShopIoT.mapper.OrderDetailMapper;
import com.thachnn.ShopIoT.mapper.OrderMapper;
import com.thachnn.ShopIoT.model.Order;
import com.thachnn.ShopIoT.model.OrderDetail;
import com.thachnn.ShopIoT.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private OrderResponse orderToOrderResponse(Order order){
        List<OrderDetail> details = order.getOrderDetailList();
        List<OrderDetailResponse> detailResponses = details.stream()
                .map(orderDetailMapper::toOrderDetailResp).toList();

        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        orderResponse.setOrderDetail(detailResponses);
        return orderResponse;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderRequest request
            //@AuthenticationPrincipal Jwt jwt)
    ){
        Order order = orderService.createNewOrder(request, /*jwt.getSubject()*/ "thachnn");
        OrderResponse orderResponse = this.orderToOrderResponse(order);
        messagingTemplate.convertAndSend("/notify/admin", orderResponse);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/code/{orderCode}")
    public ResponseEntity<?> getSingleOrder(
            @PathVariable String orderCode
    ){
        Order order = orderService.getOrderByCode(orderCode);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderToOrderResponse(order))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/code/{orderCode}/change-status")
    public ResponseEntity<?> changeStatus(
            @PathVariable String orderCode,
            @RequestBody ChangStatusRequest request
    ){
        Order order = orderService.changeStatus(orderCode, request.getNewStatus());
        OrderResponse orderResponse = this.orderToOrderResponse(order);
        String username = order.getUser().getUsername();

        messagingTemplate.convertAndSend("/notify/users/" + username,
                orderResponse.getOrderCode() + " " + orderResponse.getOrderStatus());
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(
            @AuthenticationPrincipal Jwt jwt
    ){
        List<Order> orders = orderService.getMyOrder(jwt.getSubject());
        List<OrderResponse> orderResponses = orders.stream()
                .map(this::orderToOrderResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponses)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
