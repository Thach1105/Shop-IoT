package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.OrderDetailRequest;
import com.thachnn.ShopIoT.dto.request.OrderRequest;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.OrderDetailMapper;
import com.thachnn.ShopIoT.mapper.OrderMapper;
import com.thachnn.ShopIoT.model.*;
import com.thachnn.ShopIoT.repository.OrderRepository;
import com.thachnn.ShopIoT.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@EnableMethodSecurity
public class OrderService {
    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    OrderRepository orderRepository;

    @PreAuthorize("hasRole('USER')")
    public Order createNewOrder(OrderRequest orderReq, Integer userId){

        // get status PENDING
        OrderStatus orderStatus = orderStatusRepository.findById(1).orElseThrow();
        //get user
        User user = userService.getById(userId);

        List<OrderDetailRequest> detailReqList = orderReq.getDetails();

        Order newOrder = orderMapper.toOrder(orderReq);

        List<OrderDetail> detailList = detailReqList.stream().map(
                detailReq -> {
                    Product product = productService.getSingleProduct(detailReq.getProduct());

                    OrderDetail detail = orderDetailMapper.toOrderDetail(detailReq);
                    detail.setOrder(newOrder);
                    detail.setProduct(product);
                    return detail;
                }
        ).toList();

        newOrder.setOrderStatus(orderStatus);
        newOrder.setUser(user);
        newOrder.setOrderDetailList(detailList);

        return orderRepository.save(newOrder);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Order> getAll(Integer pageNumber, Integer pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return orderRepository.findAll(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Order changeStatus(String orderCode, String orderStatus){
        OrderStatus status = orderStatusRepository.findByStatusName(orderStatus).orElseThrow();
        int check = orderRepository.updateOrderStatus(orderCode, status);
        if(check == 1) return getOrderByCode(orderCode);
        else throw new AppException(ErrorApp.CHANGE_STATUS_FAILED);
    }

    public void changPaymentStatus(Order order, boolean paymentStatus){
        order.setPaymentStatus(paymentStatus);
        orderRepository.save(order);
    }

    public void saveTransactionId(Order order, String transactionId, String paymentType){
        order.setTransactionId(transactionId);
        order.setPaymentType(paymentType);
        orderRepository.save(order);
    }

    public boolean existingByOrderCodeAndTransaction(String orderCode, String transactionId){
        return orderRepository.existsByOrderCodeAndTransactionId(orderCode, transactionId);
    }

    public Order saveCallbackPaymentData(Order order, String callbackData){
        order.setCallbackPayment(callbackData);
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderByTransactionId(String transactionId){
        return orderRepository.findByTransactionId(transactionId);
    }

    public boolean checkTransactionId(String transactionId){
        return orderRepository.existsByTransactionId(transactionId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Order getOrderById(Integer id){
        return orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.ORDER_NOT_FOUND));
    }

    public Order getOrderByCode(String orderCode){
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorApp.ORDER_NOT_FOUND));
    }

    public boolean checkOrderCode(String orderCode){
        return orderRepository.existsByOrderCode(orderCode);
    }

    @PreAuthorize("#username == principal.claims['data']['username']")
    public List<Order> getMyOrder(String username){
        return orderRepository.getAllOrderByUser(username);
    }
}
