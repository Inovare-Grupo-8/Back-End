package org.com.imaapi.config;

import com.mercadopago.MercadoPagoConfig;

public class ApiMercadoPago {
    public static void init() {
        MercadoPagoConfig.setAccessToken("SUA_ACCESS_TOKEN");
    }
}