package com.thachnn.ShopIoT.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.thachnn.ShopIoT.dto.response.StatisticResponse;

import java.util.Date;
import java.util.List;

public interface IStatisticsService {

    public List<Object[]> countOrder(Date from, Date to);

    public long countCustomer();

    public List<Object[]> getTopOrderedProduct(long from, long to);

    public StatisticResponse buildStatisticResponse(long from, long to);
}
