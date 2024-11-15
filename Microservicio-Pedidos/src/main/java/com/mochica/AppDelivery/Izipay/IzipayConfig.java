package com.mochica.AppDelivery.Izipay;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class IzipayConfig {
    @Value("${izipay.base-url}")
    private String baseUrl;

    @Value("${izipay.merchant-id}")
    private String merchantId;

    @Value("${izipay.test-key}")
    private String testKey;

    @Value("${izipay.production-key}")
    private String productionKey;

    public String getBaseUrl() { return baseUrl; }
    public String getMerchantId() { return merchantId; }
    public String getSecretKey(boolean isProduction) {
        return isProduction ? productionKey : testKey;
    }
}
