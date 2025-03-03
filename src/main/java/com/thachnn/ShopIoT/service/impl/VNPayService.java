package com.thachnn.ShopIoT.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.config.VNPayConfig;
import com.thachnn.ShopIoT.enums.PaymentMethod;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.model.Order;
import com.thachnn.ShopIoT.util.TransactionUtil;
import com.thachnn.ShopIoT.util.VNPayUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class VNPayService {

    private final OrderService orderService;

    @Autowired
    public VNPayService(OrderService orderService){
        this.orderService = orderService;
    }

    public String createPaymentByVnPay(String orderCode) throws ServletException, IOException {
        Order order = orderService.getOrderByCode(orderCode);
        if (order.isPaymentStatus() || order.getOrderStatus().getId() == 5)
            throw new AppException(ErrorApp.PAID_ORDER);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

//        String vnp_IpAddr = VNPayUtil.getIpAddress(req);
        String vnp_IpAddr = "127.0.0.1";

        long amount = order.getTotalPrice() * 100;
        Map<String, Object> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        /*create Transaction ID*/
        String vnp_TxnRef = TransactionUtil.createTransactionId(10);
        while (orderService.checkTransactionId(vnp_TxnRef)){
            vnp_TxnRef = TransactionUtil.createTransactionId(10);
        }
        orderService.saveTransactionId(order, vnp_TxnRef, PaymentMethod.VNPAY.getName());
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        String vnp_OrderInfo = "Thanh toan hoa don "+ orderCode;
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);

        //String orderType = "110001";
        String orderType = "other";
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        //Build data to hash and querystring
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayUtil.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    public ObjectNode callback(HttpServletRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode vnPayResponse = objectMapper.createObjectNode();

        try {
            Map fields = new HashMap();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    fields.put(fieldName, fieldValue);
                }
            }
            /* tạo ra đối tượng JSON cho dữ liệu callback */
            JSONObject callbackJSON = new JSONObject(fields);
            log.info("callback: {}", callbackJSON);

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");

            //Check checksum
            String signValue = VNPayUtil.hashAllFields(fields);

            if(signValue.equals(vnp_SecureHash)){
                String vnp_TxnRef = request.getParameter("vnp_TxnRef");
                String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
                String orderCode = vnp_OrderInfo.substring("Thanh+toan+hoa+don+".length());
                long amount = Long.parseLong(request.getParameter("vnp_Amount")) / 100;

                /*kiểm tra xem mã đơn hàng và mã thanh toán có khớp nhau không*/
                if(orderService.existingByOrderCodeAndTransaction(orderCode, vnp_TxnRef)){

                    Order order = orderService.getOrderByCode(orderCode);
                    order = orderService.saveCallbackPaymentData(order, callbackJSON.toString());
                    if(amount == order.getTotalPrice()){

                        boolean paymentStatus = order.isPaymentStatus();
                        if(!paymentStatus){

                            if("00".equals(request.getParameter("vnp_ResponseCode"))){
                                orderService.changPaymentStatus(order, true);
                            } else {
                                orderService.changPaymentStatus(order, false);
                            }
                            vnPayResponse.put("Message", "Confirm Success");
                            vnPayResponse.put("RspCode", "00");

                        } else {
                            vnPayResponse.put("Message", "Order already confirmed");
                            vnPayResponse.put("RspCode", "02");
                        }

                    } else {
                        vnPayResponse.put("Message", "Invalid Amount");
                        vnPayResponse.put("RspCode", "04");
                    }

                } else {
                    vnPayResponse.put("Message", "Order not Found");
                    vnPayResponse.put("RspCode", "01");
                }

            } else {
                vnPayResponse.put("Message", "Invalid Checksum");
                vnPayResponse.put("RspCode", "97");
            }

        } catch (Exception e){
            vnPayResponse.put("Message", "Unknow error");
            vnPayResponse.put("RspCode", "99");
        }

        return vnPayResponse;
    }

    public String queryOrderVNPay(String vnp_TxnRef, String vnp_TransactionNo, String vnp_CreateDateOrder)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        String vnp_RequestId = TransactionUtil.getRandomNumber(10);
        params.put("vnp_RequestId", vnp_RequestId);
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "querydr");
        params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_OrderInfo", "Request description");

        if(vnp_TransactionNo != null){
            params.put("vnp_TransactionNo", vnp_TransactionNo);
        }
        params.put("vnp_TransactionDate", vnp_CreateDateOrder);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        params.put("vnp_CreateDate", vnp_CreateDate);

        params.put("vnp_IpAddr", "127.0.0.1");

        String data = params.get("vnp_Version") + "|"
                    + params.get("vnp_Version") + "|"
                    + params.get("vnp_Command") + "|"
                    + params.get("vnp_TmnCode") + "|"
                    + params.get("vnp_TxnRef")  + "|"
                    + params.get("vnp_TransactionDate") + "|"
                    + params.get("vnp_CreateDate") + "|"
                    + params.get("vnp_IpAddr") + "|"
                    + params.get("vnp_OrderInfo");

        String vnp_SecureHash = VNPayUtil.hmacSHA512(VNPayConfig.secretKey, data);
        params.put("vnp_SecureHash", vnp_SecureHash);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(VNPayConfig.vnp_ApiUrl);
        List<NameValuePair> requestParams = new ArrayList<>();
        for (Map.Entry<String, Object> e : params.entrySet()){
            requestParams.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        httpPost.setEntity(new UrlEncodedFormEntity(requestParams));
        CloseableHttpResponse response = httpClient.execute(httpPost);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        return resultJsonStr.toString();
    }

    public boolean checksum(HttpServletRequest request){
        //Begin process return from VNPAY
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = (String) params.nextElement();
            String fieldValue = !fieldName.equals("vnp_OrderInfo")
                    ? request.getParameter(fieldName)
                    : request.getParameter(fieldName).replaceAll(" ", "+");
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                if(!fieldName.equals("paymentType")){
                    fields.put(fieldName, fieldValue);
                    System.out.println(String.format("%s: %s", fieldName, fieldValue));
                }
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        String signValue = VNPayUtil.hashAllFields(fields);
        System.out.println(signValue);
        if(signValue.equals(vnp_SecureHash)){
            return true;
        } else {
            return false;
        }
    }
}
