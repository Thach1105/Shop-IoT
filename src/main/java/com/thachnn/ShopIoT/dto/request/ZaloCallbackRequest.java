package com.thachnn.ShopIoT.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ZaloCallbackRequest {
    private String data;
    private String mac;
    private int type;

    @Getter
    @Setter
    @Builder
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CallbackData{

        @JsonProperty("app_id")
        private int appId;

        @JsonProperty("app_trans_id")
        private String appTransId;

        @JsonProperty("app_time")
        private long appTime;

        @JsonProperty("app_user")
        private String appUser;

        private JsonNode embedData;
        private JsonNode item;

        private long amount;

        @JsonProperty("zp_trans_id")
        private long zpTransId;

        @JsonProperty("server_time")
        private long serverTime;

        private int channel;

        @JsonProperty("merchant_user_id")
        private String merchantUserId;

        @JsonProperty("zp_user_id")
        private String zpUserId;

        @JsonProperty("user_fee_amount")
        private long userFeeAmount;

        @JsonProperty("discount_amount")
        private long discountAmount;

        @JsonProperty("embed_data")
        public void setEmbed_data(String embed_data) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            this.embedData = mapper.readTree(embed_data);
        }

        @JsonProperty("item")
        public void setItem(String item) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            this.item = mapper.readTree(item);
        }
    }
}
