package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.StatisticResponse;
import com.thachnn.ShopIoT.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<?> statistic(
            @RequestParam(name = "startDate") long startDate,
            @RequestParam(name = "endDate") long endDate
    ) {
        StatisticResponse response =
                statisticsService.buildStatisticResponse(startDate, endDate);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .content(response)
                        .build()
        );
    }

}
