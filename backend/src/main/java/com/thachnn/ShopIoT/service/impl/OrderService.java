package com.thachnn.ShopIoT.service.impl;

import com.thachnn.ShopIoT.dto.request.CheckPrevOrderRequest;
import com.thachnn.ShopIoT.dto.request.NotificationRequest;
import com.thachnn.ShopIoT.dto.request.OrderDetailRequest;
import com.thachnn.ShopIoT.dto.request.OrderRequest;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.OrderDetailMapper;
import com.thachnn.ShopIoT.mapper.OrderMapper;
import com.thachnn.ShopIoT.model.*;
import com.thachnn.ShopIoT.repository.OrderRepository;
import com.thachnn.ShopIoT.repository.OrderStatusRepository;
import com.thachnn.ShopIoT.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@EnableMethodSecurity
public class OrderService implements IOrderService {

    private final OrderStatusRepository orderStatusRepository;
    private final ProductService productService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;

    public OrderService(
            OrderStatusRepository orderStatusRepository,
            ProductService productService,
            UserService userService,
            OrderMapper orderMapper,
            OrderDetailMapper orderDetailMapper,
            OrderRepository orderRepository,
            NotificationService notificationService,
            SimpMessagingTemplate  messagingTemplate,
            EmailService emailService
    ){
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.userService = userService;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.orderStatusRepository = orderStatusRepository;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public Order createNewOrder(OrderRequest orderReq, Integer userId){

        // get status PENDING
        OrderStatus orderStatus = orderStatusRepository.findByStatusName("PENDING").orElseThrow();
        //get user
        User user = userService.getById(userId);

        List<OrderDetailRequest> detailReqList = orderReq.getDetails();

        Order newOrder = orderMapper.toOrder(orderReq);

        //Check the quantity of products in stock for each product in the order detail
        List<CheckPrevOrderRequest.PrevOrder> prevOrderList = detailReqList.stream().map(
                detailReq -> {
                    CheckPrevOrderRequest.PrevOrder prevOrder = new CheckPrevOrderRequest.PrevOrder();
                    prevOrder.setProductId(detailReq.getProduct());
                    prevOrder.setQuantity(detailReq.getQuantity());
                    return prevOrder;
                }
        ).toList();
        checkPreviousOrder(prevOrderList);

        List<OrderDetail> detailList = detailReqList.stream().map(
                detailReq -> {
                    Product product = productService.getSingleProduct(detailReq.getProduct());
                    product.setSalesNumber(product.getSalesNumber() + detailReq.getQuantity());
                    productService.subStock(product.getId(), detailReq.getQuantity());

                    OrderDetail detail = orderDetailMapper.toOrderDetail(detailReq);
                    detail.setOrder(newOrder);
                    detail.setProduct(product);
                    return detail;
                }
        ).toList();

        newOrder.setOrderStatus(orderStatus);
        newOrder.setUser(user);
        newOrder.setOrderDetailList(detailList);

        var returnOrder = orderRepository.save(newOrder);

        Notification notification = notificationService.create(
                NotificationRequest.builder()
                        .orderCode(returnOrder.getOrderCode())
                        .message("Bạn có đơn hàng mới")
                        .sender(userId)
                        .build());

        messagingTemplate.convertAndSend("/topic/admin", notification);
        emailService.sendOrderConfirmEmail(user, returnOrder);

        return returnOrder;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Order> getAll(Integer pageNumber, Integer pageSize){
        Sort sort = Sort.by(Sort.Direction.DESC, "orderTime");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return orderRepository.findAll(pageable);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Order changeStatus(String orderCode, String orderStatus){
        OrderStatus status = orderStatusRepository.findByStatusName(orderStatus).orElseThrow();
        int check = orderRepository.updateOrderStatus(orderCode, status);
        if(check == 1) return getOrderByCode(orderCode);
        else throw new AppException(ErrorApp.CHANGE_STATUS_FAILED);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Order changePaymentStatusByAdmin(String orderCode, boolean paymentStatus){
        int check = orderRepository.updateOrderPaymentStatus(orderCode, paymentStatus);
        if(check == 1) return getOrderByCode(orderCode);
        else throw new AppException(ErrorApp.CHANGE_STATUS_FAILED);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public boolean checkPreviousOrder(List<CheckPrevOrderRequest.PrevOrder> listProduct){
        for(var i : listProduct){
            Product p = productService.getSingleProduct(i.getProductId());
            if(p.getStock() < i.getQuantity()) {
                throw new AppException(
                        ErrorApp.PRODUCT_STOCK_NOT_NULL,
                        String.format("Sản phẩm %s không đủ số lượng cần mua trong kho", p.getName()));
            }
        }

        return true;
    }

    @Override
    public void changPaymentStatus(Order order, boolean paymentStatus){
        order.setPaymentStatus(paymentStatus);
        orderRepository.save(order);
    }

    @Override
    public void saveTransactionId(Order order, String transactionId, String paymentType){
        order.setTransactionId(transactionId);
        order.setPaymentType(paymentType);
        orderRepository.save(order);
    }

    @Override
    public boolean existingByOrderCodeAndTransaction(String orderCode, String transactionId){
        return orderRepository.existsByOrderCodeAndTransactionId(orderCode, transactionId);
    }

    @Override
    public Order saveCallbackPaymentData(Order order, String callbackData){
        order.setCallbackPayment(callbackData);
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrderByTransactionId(String transactionId){
        return orderRepository.findByTransactionId(transactionId);
    }

    @Override
    public boolean checkTransactionId(String transactionId){
        return orderRepository.existsByTransactionId(transactionId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Order> getOrdersByUser(Integer userId, Integer pageNum, Integer pageSize){
        User user = userService.getById(userId);
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return orderRepository.getAllOrderByUserId(userId, pageable);

    }

    /*@PreAuthorize("hasRole('ADMIN')")
    public Order getOrderById(Integer id){
        return orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.ORDER_NOT_FOUND));
    }*/

    @Override
    public Order getOrderByCode(String orderCode){
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorApp.ORDER_NOT_FOUND));
    }

    /*public boolean checkOrderCode(String orderCode){
        return orderRepository.existsByOrderCode(orderCode);
    }*/

    @Override
    @PreAuthorize("#username == principal.claims['data']['username']")
    public List<Order> getMyOrder(String username){
        return orderRepository.getAllOrderByUser(username);
    }

    @Override
    @PreAuthorize("#username == principal.claims['data']['username']")
    public Order cancelOrder(String orderCode, String username){
        OrderStatus cancelStatus = orderStatusRepository.findById(5).orElseThrow();
        List<Order> orders = orderRepository.getAllOrderByUser(username);

        for(var o : orders){
            if(o.getOrderCode().equals(orderCode)){
                if((o.getOrderStatus().getId() == 1 || o.getOrderStatus().getId() == 2) && !o.isPaymentStatus()){
                    o.setOrderStatus(cancelStatus);
                    return orderRepository.save(o);
                } else
                {
                    throw new AppException(ErrorApp.ORDER_NOT_CANCEL);
                }
            }
        }

        throw new AppException(ErrorApp.ACCESS_DENIED);
    }
}
