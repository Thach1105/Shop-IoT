package com.thachnn.ShopIoT.repository.httpclient;


import com.thachnn.ShopIoT.dto.request.ExchangeTokenRequest;
import com.thachnn.ShopIoT.dto.response.ExchangeTokenResponse;
import com.thachnn.ShopIoT.dto.response.OutboundUser;
import feign.QueryMap;
import org.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-user-client", url = "https://www.googleapis.com")
public interface OutboundUserClient {

    @GetMapping(value = "/oauth2/v1/userinfo")
    OutboundUser getUserInfo(
            @RequestParam("alt") String alt,
            @RequestParam("access_token") String accessToken
    );

}
