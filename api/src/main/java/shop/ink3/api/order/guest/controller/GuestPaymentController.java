package shop.ink3.api.order.guest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.order.guest.dto.GuestPaymentConfirmRequest;
import shop.ink3.api.order.guest.service.GuestPaymentService;
import shop.ink3.api.payment.dto.PaymentResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/guest-payment")
public class GuestPaymentController {
    private final GuestPaymentService guestPaymentService;


    // 결제 승인 API 호출 및 결과 저장
    @PostMapping("/confirm")
    public ResponseEntity<CommonResponse<PaymentResponse>> confirmPayment(
            @RequestBody GuestPaymentConfirmRequest confirmRequest
    ) {
        log.info("payType={}", confirmRequest.paymentType());
        String paymentApproveResponse = guestPaymentService.callPaymentAPI(confirmRequest);
        PaymentResponse paymentResponse = guestPaymentService.createPayment(confirmRequest, paymentApproveResponse);
        return ResponseEntity.ok(CommonResponse.success(paymentResponse));
    }

    // 결제 실패 처리 (회원)
    @PostMapping("/{orderId}/fail")
    public ResponseEntity<CommonResponse<Void>> failPayment(@PathVariable long orderId){
        guestPaymentService.failPayment(orderId);
        return ResponseEntity.noContent().build();
    }

}
