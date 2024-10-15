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
import com.thachnn.ShopIoT.util.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    private final String PAGE_DEFAULT = "1";
    private final String SIZE_DEFAULT = "20";

    private OrderResponse orderToOrderResponse(Order order){
        List<OrderDetail> details = order.getOrderDetailList();
        List<OrderDetailResponse> detailResponses = details.stream()
                .map(orderDetailMapper::toOrderDetailResp).toList();

        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        orderResponse.setOrderDetail(detailResponses);
        return orderResponse;
    }

    @PostMapping /*checked*/
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userIdLong = (Long) jwt.getClaimAsMap("data").get("id");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;

        Order order = orderService.createNewOrder(request,userId);
        OrderResponse orderResponse = this.orderToOrderResponse(order);
        messagingTemplate.convertAndSend("/topic/admin", orderResponse);

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

    @GetMapping("/all") /*checked*/
    public ResponseEntity<?> getAllOrders(
            @RequestParam(name = "page", defaultValue = PAGE_DEFAULT) Integer pageNum,
            @RequestParam(name = "size", defaultValue = SIZE_DEFAULT) Integer pageSize
    ){
        Page<Order> orderPage = orderService.getAll(pageNum-1, pageSize);
        PageInfo pageInfo = PageInfo.builder()
                .page(pageNum)
                .size(pageSize)
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build();

        List<Order> orders = orderPage.getContent();
        List<OrderResponse> orderResponses = orders.stream()
                .map(this::orderToOrderResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponses)
                .pageDetails(pageInfo)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/code/{orderCode}/change-status") /*checked*/
    public ResponseEntity<?> changeStatus(
            @PathVariable String orderCode,
            @RequestBody ChangStatusRequest request
    ){
        Order order = orderService.changeStatus(orderCode, request.getNewStatus());
        OrderResponse orderResponse = this.orderToOrderResponse(order);
        String username = order.getUser().getUsername();

        messagingTemplate.convertAndSend("/notifications/user/" + username,
                orderResponse.getOrderCode() + " " + orderResponse.getOrderStatus());
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/my-orders") /*checked*/
    public ResponseEntity<?> getMyOrders(
            @AuthenticationPrincipal Jwt jwt
    ){
        String username = (String) jwt.getClaimAsMap("data").get("username");
        List<Order> orders = orderService.getMyOrder(username);
        List<OrderResponse> orderResponses = orders.stream()
                .map(this::orderToOrderResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponses)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
