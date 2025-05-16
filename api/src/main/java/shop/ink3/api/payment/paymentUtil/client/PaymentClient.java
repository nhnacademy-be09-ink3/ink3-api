package shop.ink3.api.payment.paymentUtil.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "paymentClient", url = "${payment.toss.confirm_url}")
public interface PaymentClient {

    @PostMapping
    String confirmPayment(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, Object> body
    );
}