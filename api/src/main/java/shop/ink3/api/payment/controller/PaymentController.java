package shop.ink3.api.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.payment.dto.OrderFormCreateRequest;
import shop.ink3.api.payment.dto.PaymentConfirmRequest;
import shop.ink3.api.payment.dto.PaymentResponse;
import shop.ink3.api.payment.entity.Payment;
import shop.ink3.api.payment.service.PaymentService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;


    // fix : 반환값 front-server 랑 맞춰야함
    @PostMapping("/order")
    public ResponseEntity<CommonResponse<Void>> createOrderInfo(@RequestBody OrderFormCreateRequest orderFormCreateRequest){
        paymentService.createOrderForm(orderFormCreateRequest);
        return ResponseEntity.noContent().build();
    }


    // 결제 승인 API 호출 및 결과 저장까지
    @PostMapping("/confirm")
    public ResponseEntity<CommonResponse<PaymentResponse>> paymentConfirm(@RequestBody PaymentConfirmRequest confirmRequest){
        log.info("payType={}",confirmRequest.paymentType());
        Payment payment = paymentService.callPaymentAPI(confirmRequest);

        // fix : 어떤 결과값을 넘겨야하나?
        return ResponseEntity
                .ok(CommonResponse.success(paymentService.createPayment(payment)));
    }
}
