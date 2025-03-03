package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.CheckPrevOrderRequest;
import com.thachnn.ShopIoT.dto.request.OrderRequest;
import com.thachnn.ShopIoT.model.Order;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IOrderService {

    public Order createNewOrder(OrderRequest orderReq, Integer userId);

    public Page<Order> getOrdersByUser(Integer userId, Integer pageNum, Integer pageSize);

    public Order getOrderByCode(String orderCode);

    public List<Order> getMyOrder(String username);

    public Order cancelOrder(String orderCode, String username);

    public Page<Order> getAll(Integer pageNumber, Integer pageSize);

    public Order changeStatus(String orderCode, String orderStatus);

    public Order changePaymentStatusByAdmin(String orderCode, boolean paymentStatus);

    public boolean checkPreviousOrder(List<CheckPrevOrderRequest.PrevOrder> listProduct);

    public void changPaymentStatus(Order order, boolean paymentStatus);

    public void saveTransactionId(Order order, String transactionId, String paymentType);

    public boolean existingByOrderCodeAndTransaction(String orderCode, String transactionId);

    public Order saveCallbackPaymentData(Order order, String callbackData);

    public Optional<Order> getOrderByTransactionId(String transactionId);

    public boolean checkTransactionId(String transactionId);
}
