package com.mochica.AppDelivery.Service;

import java.util.Map;

public interface IpnVerificationService {
    boolean verifySignature(Map<String, String> payload);
}
