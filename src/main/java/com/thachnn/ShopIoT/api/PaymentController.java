package com.thachnn.ShopIoT.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.model.Order;
import com.thachnn.ShopIoT.service.PaymentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    PaymentService paymentService;

    @GetMapping("/vn-pay/pay-order/{orderCode}")
    public ResponseEntity<?> createPaymentByVNPay(
            @PathVariable("orderCode")String orderCode
    ) throws ServletException, IOException {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(paymentService.createPaymentByVnPay(orderCode))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/vn-pay/call-back")
    public ResponseEntity<?> transaction(
            HttpServletRequest request
    ) throws UnsupportedEncodingException {
       paymentService.checksum(request);

        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/vn-pay/IPN")
    public ResponseEntity<?> codeIpn(
            HttpServletRequest request
    ) throws UnsupportedEncodingException {

        ObjectNode objectNode = paymentService.checksum(request);
        return ResponseEntity.ok(objectNode);
    }
}
