package com.thachnn.ShopIoT.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.dto.request.ZaloCallbackRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.service.VNPayService;
import com.thachnn.ShopIoT.service.ZaloPayService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    VNPayService VNPayService;

    @Autowired
    ZaloPayService zaloPayService;

    @GetMapping("/vn-pay/pay-order/{orderCode}")
    public ResponseEntity<?> createPaymentByVNPay(
            @PathVariable("orderCode")String orderCode
    ) throws ServletException, IOException {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(VNPayService.createPaymentByVnPay(orderCode))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /*@GetMapping("/vn-pay/call-back")
    public ResponseEntity<?> vnpayCallback(
            HttpServletRequest request
    ){
       VNPayService.checksum(request);

        return ResponseEntity.ok().body("OK");
    }*/

    @GetMapping("/vn-pay/IPN")
    public ResponseEntity<?> codeIpn(
            HttpServletRequest request
    ) throws UnsupportedEncodingException {

        ObjectNode objectNode = VNPayService.checksum(request);
        return ResponseEntity.ok(objectNode);
    }

    /*@PostMapping("/vn-pay/query-order")
    public ResponseEntity<?> queryOrderVnPay(){
        @RequestBody
    }*/

    @GetMapping("/zalo-pay/pay-order/{orderCode}")
    public ResponseEntity<?> createPaymentByZaloPay(
            @PathVariable String orderCode,
            @AuthenticationPrincipal Jwt jwt
            ) throws IOException {
        String username = (String) jwt.getClaimAsMap("data").get("username");
        String paymentObject = zaloPayService.createOrder(orderCode, username);

        ObjectMapper objectMapper = new ObjectMapper();
        var paymentResponse = objectMapper.readValue(paymentObject, Map.class);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(paymentResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/zalo-pay/call-back")
    public ResponseEntity<?> zalopayCallback(
            @RequestBody ZaloCallbackRequest requestData
            ) throws JsonProcessingException {
        System.out.println(requestData.toString());
       JSONObject resObject = zaloPayService.handleCallback(requestData);

        ObjectMapper objectMapper = new ObjectMapper();

       return ResponseEntity.ok(objectMapper.readValue(resObject.toString(), Map.class));

    }

    @PostMapping("/zalo-pay/check-status-order/{transId}")
    public ResponseEntity<?> checkStatusOrderZaloPay(
        @PathVariable String transId
    ) throws URISyntaxException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return ResponseEntity.ok(objectMapper.readValue(zaloPayService.checkOrderStatus(transId), Map.class));
    }
}
