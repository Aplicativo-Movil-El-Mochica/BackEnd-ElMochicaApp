package com.mochica.AppDelivery.Service;

import com.mochica.AppDelivery.DTO.FormtokenResponseDTO;
import com.mochica.AppDelivery.DTO.InitiatePaymentDTO;

public interface OrderService {

    FormtokenResponseDTO generateFormToken(InitiatePaymentDTO initiatePaymentDTO) throws Exception;
}
