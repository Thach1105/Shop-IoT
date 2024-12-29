package com.thachnn.ShopIoT.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.dto.response.StatisticResponse;
import com.thachnn.ShopIoT.repository.OrderDetailRepository;
import com.thachnn.ShopIoT.repository.OrderRepository;
import com.thachnn.ShopIoT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StatisticsService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Object[]> countOrder(Date from, Date to) {
        List<Object[]> res = orderRepository.countOrderByDate(from, to);
        return res;
    }

    public long countCustomer() {
        return userRepository.countCustomer();
    }

    public List<Object[]> getTopOrderedProduct(long from, long to){
        Date startDate = new Date(from);
        Date endDate = new Date(to + 86400000 - 1);

       return orderDetailRepository.getTopOrderedProduct(startDate, endDate);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public StatisticResponse buildStatisticResponse(long from, long to) {
        Date startDate = new Date(from);
        Date endDate = new Date(to + 86400000 - 1);

        List<Object[]> resultQuery = countOrder(startDate, endDate);
        var line1 = resultQuery.get(0);
        long numOfCustomer = countCustomer();
        StatisticResponse statisticResponse = new StatisticResponse();
        statisticResponse.setTotalOrder((Long) line1[0]);
        statisticResponse.setTotalPrice((Long) line1[1]);
        statisticResponse.setTotalProduct((Long) line1[2]);
        statisticResponse.setTotalCustomer((Long) numOfCustomer);

        List<Object[]> query2 = getTopOrderedProduct(from, to);
        List<JsonNode> listTopProduct = getJsonNodes(query2);
        statisticResponse.setTopOrderedProduct(listTopProduct);

        return statisticResponse;
    }

    private List<JsonNode> getJsonNodes(List<Object[]> query2) {
        String src = "https://shopiot-files.s3.ap-southeast-1.amazonaws.com/products-image/";
        List<JsonNode> listTopProduct = new ArrayList<>();
        for(var item : query2){
            String urlImage = src + item[0] + "/" + item[1];
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("productId",(Long) item[0]);
            objectNode.put("productImage",urlImage);
            objectNode.put("productName",(String) item[2]);
            objectNode.put("price",(Long) item[3]);
            objectNode.put("cost",(Long) item[4]);
            objectNode.put("sku",(String) item[5]);
            objectNode.put("slug",(String) item[6]);
            objectNode.put("totalOrdered",(BigDecimal) item[7]);

            listTopProduct.add(objectNode);
        }
        return listTopProduct;
    }
}
