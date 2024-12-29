package com.thachnn.ShopIoT.api;
import com.thachnn.ShopIoT.dto.request.ChangStatusRequest;
import com.thachnn.ShopIoT.dto.request.CheckPrevOrderRequest;
import com.thachnn.ShopIoT.dto.request.OrderRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.OrderDetailResponse;
import com.thachnn.ShopIoT.dto.response.OrderResponse;
import com.thachnn.ShopIoT.mapper.OrderDetailMapper;
import com.thachnn.ShopIoT.mapper.OrderMapper;
import com.thachnn.ShopIoT.model.Order;
import com.thachnn.ShopIoT.model.OrderDetail;
import com.thachnn.ShopIoT.service.EmailService;
import com.thachnn.ShopIoT.service.OrderService;
import com.thachnn.ShopIoT.util.PageInfo;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final SimpMessagingTemplate messagingTemplate;

    private final String PAGE_DEFAULT = "1";
    private final String SIZE_DEFAULT = "20";

    public OrderController(
            OrderService orderService,
            OrderMapper orderMapper,
            OrderDetailMapper orderDetailMapper,
            SimpMessagingTemplate messagingTemplate,
            EmailService emailService
    ){
        this.orderService = orderService;
        this.orderMapper =  orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.messagingTemplate = messagingTemplate;
    }

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
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userIdLong = (Long) jwt.getClaimAsMap("data").get("id");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;

        Order order = orderService.createNewOrder(request,userId);
        OrderResponse orderResponse = this.orderToOrderResponse(order);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/checkPreOrder")
    public ResponseEntity<?> checkAndLockProduct(
            @RequestBody CheckPrevOrderRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        boolean check = orderService.checkPreviousOrder(request.getPrevOrderList());

        if(check) {
            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .success(true)
                    .content("Check trước khi đặt hàng thành công")
                    .build();
            return ResponseEntity.ok().body(apiResponse);
        }

        return ResponseEntity.badRequest().build();
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
            @RequestParam(name = "pageNumber", defaultValue = PAGE_DEFAULT) Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = SIZE_DEFAULT) Integer pageSize
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

    @PutMapping("/code/{orderCode}/change-payment-status") /*checked*/
    public ResponseEntity<?> changePaymentStatus(
            @PathVariable String orderCode,
            @RequestParam(name = "paymentStatus") boolean status
    ){
        Order order = orderService.changePaymentStatusByAdmin(orderCode, status);
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

    @GetMapping("/user-id={userId}")
    public ResponseEntity<?> getOrdersByUser(
            @PathVariable("userId") Integer userId,
            @RequestParam(name = "pageNumber", defaultValue = PAGE_DEFAULT) Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = SIZE_DEFAULT) Integer pageSize
    ){
        Page<Order> userOrders = orderService.getOrdersByUser(userId, pageNum-1, pageSize);
        PageInfo pageInfo = PageInfo.builder()
                .page(pageNum)
                .size(pageSize)
                .totalElements(userOrders.getTotalElements())
                .totalPages(userOrders.getTotalPages())
                .build();

        List<Order> orders = userOrders.getContent();
        List<OrderResponse> orderResponses = orders.stream()
                .map(this::orderToOrderResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponses)
                .pageDetails(pageInfo)
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

    @PutMapping("/my-order/cancel/{orderCode}")
    public ResponseEntity<?> getMyOrder(
            @PathVariable("orderCode") String orderCode,
            @AuthenticationPrincipal Jwt jwt
    ){
        String username = (String) jwt.getClaimAsMap("data").get("username");
        OrderResponse orderResponse = this.orderToOrderResponse(orderService.cancelOrder(orderCode, username));

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
