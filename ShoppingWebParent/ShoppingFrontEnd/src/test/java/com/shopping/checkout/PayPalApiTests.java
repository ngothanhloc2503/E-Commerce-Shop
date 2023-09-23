package com.shopping.checkout;

import com.shopping.checkout.paypal.PayPalOrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;


public class PayPalApiTests {
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID = "AZhSb89VIpDMaSRwg4y61v47EaAjPwkY-aDVBpLB6yZV2FJhajLFfA0WNo3dVf9kKQTdFc7ve8Jzv_-6";
    private static final String CLIENT_SECRET = "EJ_s-yp8AmKCZdT5XXKoaQwK8PQ0dw7tIrMKqccH9DyEyvqt8bVA__1Br1uabrAm7kKWp3aoMdCl7Wnt";

    @Test
    public void testGetOrderDetails() {
        String orderId = "9PY44659YD576453U";
        String requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();

        System.out.println("Order ID: " + orderResponse.getId());
        System.out.println("Validated: " + orderResponse.validate(orderId));
    }
}
