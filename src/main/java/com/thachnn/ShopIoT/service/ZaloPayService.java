package com.thachnn.ShopIoT.service;// Java version "1.8.0_201"
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thachnn.ShopIoT.config.ZaloPayConfig;
import com.thachnn.ShopIoT.dto.request.ZaloCallbackRequest;
import com.thachnn.ShopIoT.enums.PaymentMethod;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.model.Order;
import com.thachnn.ShopIoT.model.OrderDetail;
import com.thachnn.ShopIoT.util.HMACUtil;
import com.thachnn.ShopIoT.util.TransactionUtil;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair; // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject; // https://mvnrepository.com/artifact/org.json/json
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Slf4j
@Service
public class ZaloPayService {

    @Autowired
    OrderService orderService;

    private final String redirectURL =
            "https://6411-2401-d800-376-844f-7160-ec90-3f10-f9a1.ngrok-free.app/thanh-toan?paymentType=ZALOPAY";


    public String createOrder(String orderCode, String app_user) throws IOException {
        Order order = orderService.getOrderByCode(orderCode);
        if (order.isPaymentStatus() || order.getOrderStatus().getId() == 5)
            throw new AppException(ErrorApp.PAID_ORDER);

        final Map<String, Object> embed_data = new HashMap<>() {{
            put("preferred_payment_method", new ArrayList<>());
            put("redirecturl", redirectURL);
        }};

        final JSONArray item = new JSONArray();
        List<OrderDetail> details = order.getOrderDetailList();
        for (var i : details) {
            JSONObject itemObject = new JSONObject();

            itemObject.put("id", i.getProduct().getId());
            itemObject.put("product_name", i.getProduct().getSku());
            itemObject.put("quantity", i.getQuantity());
            itemObject.put("price", i.getTotalPrice());

            item.put(itemObject);
        }

        Map<String, Object> orderParams = new HashMap<>() {
        };
        orderParams.put("app_id", ZaloPayConfig.app_id);

        String trans_id = TransactionUtil.createTransactionId(10);
        while (orderService.checkTransactionId(trans_id)){
            trans_id = TransactionUtil.createTransactionId(10);
        }
        orderService.saveTransactionId(order, trans_id, PaymentMethod.ZALOPAY.getName());
        orderParams.put("app_trans_id", trans_id); // translation missing: vi.docs.shared.sample_code.comments.app_trans_id

        orderParams.put("app_time", System.currentTimeMillis()); // miliseconds
        orderParams.put("app_user", app_user);
        orderParams.put("amount", order.getTotalPrice());
        orderParams.put("description", "Thanh toan don hang #" + orderCode);
        orderParams.put("bank_code", "");
        orderParams.put("item", item.toString());
        orderParams.put("embed_data", new JSONObject(embed_data).toString());
        orderParams.put("phone", order.getPhone());
//        orderParams.put("address", order.getAddress());
        String callbackURL = "https://61b7-2402-800-61c7-c589-993e-f03c-5aa4-e574.ngrok-free.app/shopIoT/api";
        orderParams.put("callback_url", callbackURL + "/payment/zalo-pay/call-back");

        // app_id +”|”+ app_trans_id +”|”+ appuser +”|”+ amount +"|" + app_time +”|”+ embed_data +"|" +item
        String data = orderParams.get("app_id") + "|"
                + orderParams.get("app_trans_id") + "|"
                + orderParams.get("app_user") + "|"
                + orderParams.get("amount") + "|"
                + orderParams.get("app_time") + "|"
                + orderParams.get("embed_data") + "|"
                + orderParams.get("item");
        orderParams.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.key1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ZaloPayConfig.endpoint + "/create");

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : orderParams.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        // Content-Type: application/x-www-form-urlencoded
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        return resultJsonStr.toString();
    }

    public JSONObject handleCallback(ZaloCallbackRequest request) {
        JSONObject result = new JSONObject();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ZaloCallbackRequest.CallbackData data =
                    objectMapper.readValue(request.getData(), ZaloCallbackRequest.CallbackData.class);

            String reqmac = request.getMac();
            String dataStr = request.getData();
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            hmacSHA256.init(new SecretKeySpec(ZaloPayConfig.key2.getBytes(), "HmacSHA256"));
            byte[] hashBytes = hmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
            String transactionId = data.getAppTransId();

            if (!reqmac.equals(mac)) {
                //callback không hợp lệ
                result.put("return_code", -1);
                result.put("return_message", "mac not equal");
            } else {
                // thanh toán thành công
                // merchant cập nhật trạng thái cho đơn hàng
                Order order = orderService.getOrderByTransactionId(transactionId).orElseThrow(Exception::new);
                String callbackDataJSON = objectMapper.writeValueAsString(data);
                orderService.changPaymentStatus(order, true);
                orderService.saveCallbackPaymentData(order, callbackDataJSON);

                log.info("update order's payment status = true where app_trans_id = {}", data.getAppTransId());
                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0); // ZaloPay server sẽ callback lại (tối đa 3 lần)
            result.put("return_message", ex.getMessage());
        }

        return result;
    }
    public boolean checksum(Map<String, String> data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        hmacSHA256.init(new SecretKeySpec(ZaloPayConfig.key2.getBytes(), "HmacSHA256"));

        String checksumData = data.get("appid") + "|" +
                data.get("apptransid") + "|" +
                data.get("pmcid") + "|" +
                data.get("bankcode") + "|" +
                data.get("amount") + "|" +
                data.get("discountamount") + "|" +
                data.get("status");
        byte[] checksumBytes = hmacSHA256.doFinal(checksumData.getBytes());
        String checksum = DatatypeConverter.printHexBinary(checksumBytes).toLowerCase();

        if (!checksum.equals(data.get("checksum"))) {
            return false;
        } else {
            // kiểm tra xem đã nhận được callback hay chưa, nếu chưa thì tiến hành gọi API truy vấn trạng thái thanh toán của đơn hàng để lấy kết quả cuối cùng
            return true;
        }
    }

    public String checkOrderStatus(String app_trans_id)
            throws URISyntaxException, IOException {

        // app_id|app_trans_id|key1
        String data = ZaloPayConfig.app_id + "|" + app_trans_id + "|" + ZaloPayConfig.key1;
        log.info("data: {}", data);
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.key1, data);
        log.info("mac: {}", mac);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("app_id", ZaloPayConfig.app_id));
        params.add(new BasicNameValuePair("app_trans_id", app_trans_id));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(ZaloPayConfig.endpoint + "/query");
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        return resultJsonStr.toString();
    }
}