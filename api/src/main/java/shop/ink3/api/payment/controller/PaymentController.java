package shop.ink3.api.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.payment.dto.PaymentConfirmRequest;
import shop.ink3.api.payment.dto.PaymentResponse;
import shop.ink3.api.payment.entity.Payment;
import shop.ink3.api.payment.service.PaymentService;
import shop.ink3.api.user.point.dto.PointHistoryCreateRequest;
import shop.ink3.api.user.point.entity.PointHistory;
import shop.ink3.api.user.point.eventListener.PointEventListener;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    // 결제 승인 API 호출 및 결과 저장
    @PostMapping("/confirm")
    public ResponseEntity<CommonResponse<PaymentResponse>> confirmPayment(@RequestBody PaymentConfirmRequest confirmRequest){
        log.info("payType={}",confirmRequest.paymentType());
        Payment payment = paymentService.callPaymentAPI(confirmRequest);
        PaymentResponse paymentResponse = paymentService.createPayment(payment);
        return ResponseEntity.ok(CommonResponse.success(paymentResponse));
    }

    // 결제 결과 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<PaymentResponse>> getPayment(@PathVariable long orderId){
        return ResponseEntity
                .ok(CommonResponse.success(paymentService.getPayment(orderId)));
    }

    // 결제 취소
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<CommonResponse<Void>> cancelPayment(
            @PathVariable long orderId,
            @RequestHeader("X-User-Id") long userId){
        paymentService.cancelPayment(orderId, userId);
        return ResponseEntity.noContent().build();
    }
}
