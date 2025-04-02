package org.com.imaapi.ConfigMercadoPago;
import com.mercadopago.MercadoPago;

public class ApiMercadoPago {
    public static void init() {
        MercadoPago.SDK.setAccessToken("SUA_ACCESS_TOKEN");
    }
}
