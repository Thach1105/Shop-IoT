package com.thachnn.ShopIoT.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.config.VNPayConfig;
import com.thachnn.ShopIoT.dto.response.PaymentResponse;
import com.thachnn.ShopIoT.model.Order;
import com.thachnn.ShopIoT.util.VNPayUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    OrderService orderService;

    public String createPaymentByVnPay(String orderCode) throws ServletException, IOException {
        Order order = orderService.getOrderByCode(orderCode);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        /*String vnp_OrderInfo = req.getParameter("vnp_OrderInfo");
        String orderType = req.getParameter("ordertype");*/

//        String vnp_IpAddr = VNPayUtil.getIpAddress(req);
        String vnp_IpAddr = "127.0.0.1";


//        int amount = Integer.parseInt(req.getParameter("amount")) * 100;
        long amount = order.getTotalPrice() * 100;
        Map vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        /*Tùy chọn, nếu không có tham số vnp_BankCode sẽ chuyển hướng người dùng
        sang VNPAY chọn phương thức thanh toán*/
        /*
        String bank_code = req.getParameter("bankcode");
        if (bank_code != null && !bank_code.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bank_code);
        }*/

        String vnp_TxnRef = VNPayUtil.getRandomNumber(10);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        orderService.saveTransactionNo(orderCode, vnp_TxnRef);

        String vnp_OrderInfo = "Thanh toan hoa don "+ orderCode;
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);

        //String orderType = "110001";
        String orderType = "other";
        vnp_Params.put("vnp_OrderType", orderType);

        /*String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }*/
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        //Billing
        /*vnp_Params.put("vnp_Bill_Mobile", req.getParameter("txt_billing_mobile"));
        vnp_Params.put("vnp_Bill_Email", req.getParameter("txt_billing_email"));
        String fullName = (req.getParameter("txt_billing_fullname")).trim();
        if (fullName != null && !fullName.isEmpty()) {
            int idx = fullName.indexOf(' ');
            String firstName = fullName.substring(0, idx);
            String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
            vnp_Params.put("vnp_Bill_FirstName", firstName);
            vnp_Params.put("vnp_Bill_LastName", lastName);

        }
        vnp_Params.put("vnp_Bill_Address", req.getParameter("txt_inv_addr1"));
        vnp_Params.put("vnp_Bill_City", req.getParameter("txt_bill_city"));
        vnp_Params.put("vnp_Bill_Country", req.getParameter("txt_bill_country"));
        if (req.getParameter("txt_bill_state") != null && !req.getParameter("txt_bill_state").isEmpty()) {
            vnp_Params.put("vnp_Bill_State", req.getParameter("txt_bill_state"));
        }
        // Invoice
        vnp_Params.put("vnp_Inv_Phone", req.getParameter("txt_inv_mobile"));
        vnp_Params.put("vnp_Inv_Email", req.getParameter("txt_inv_email"));
        vnp_Params.put("vnp_Inv_Customer", req.getParameter("txt_inv_customer"));
        vnp_Params.put("vnp_Inv_Address", req.getParameter("txt_inv_addr1"));
        vnp_Params.put("vnp_Inv_Company", req.getParameter("txt_inv_company"));
        vnp_Params.put("vnp_Inv_Taxcode", req.getParameter("txt_inv_taxcode"));
        vnp_Params.put("vnp_Inv_Type", req.getParameter("cbo_inv_type"));*/

        //Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
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
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
//        com.google.gson.JsonObject job = new JsonObject();
//        job.addProperty("code", "00");
//        job.addProperty("message", "success");
//        job.addProperty("data", paymentUrl);
//        Gson gson = new Gson();
//        resp.getWriter().write(gson.toJson(job));

        return paymentUrl;
    }

    public ObjectNode checksum(HttpServletRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode vnPayResponse = objectMapper.createObjectNode();
        try {
            Map fields = new HashMap();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }


            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType"))
            {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash"))
            {
                fields.remove("vnp_SecureHash");
            }

            //Check checksum
            String signValue = VNPayUtil.hashAllFields(fields);

            if(signValue.equals(vnp_SecureHash)){
                String vnp_TxnRef = request.getParameter("vnp_TxnRef");
                String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
                String orderCode = vnp_OrderInfo.substring("Thanh+toan+hoa+don+".length());
                Order order = orderService.getOrderByCode(orderCode);
                long amount = Long.parseLong(request.getParameter("vnp_Amount")) / 100;

                boolean checkOrderId = orderService.checkOrderCode(orderCode);
                if(checkOrderId && vnp_TxnRef.equals(order.getTransactionReference())){
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
}
