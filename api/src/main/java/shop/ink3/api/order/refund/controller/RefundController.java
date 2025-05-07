package shop.ink3.api.order.refund.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.refund.dto.RefundCreateRequest;
import shop.ink3.api.order.refund.dto.RefundResponse;
import shop.ink3.api.order.refund.dto.RefundUpdateRequest;
import shop.ink3.api.order.refund.service.RefundService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/refunds")
public class RefundController {
    private final RefundService refundService;

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<RefundResponse>> getRefund(
            @PathVariable long orderId) {
        return ResponseEntity.ok(CommonResponse.success(refundService.getRefund(orderId)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CommonResponse<PageResponse<RefundResponse>>> getUserRefundList(
            @PathVariable long userId, Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(refundService.getUserRefundList(userId, pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<RefundResponse>> createRefund(
            @RequestBody RefundCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(refundService.createRefund(request)));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<CommonResponse<RefundResponse>> updateRefund(
            @PathVariable long orderId,
            @RequestBody RefundUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(refundService.updateRefund(orderId, request)));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteRefund(@PathVariable long orderId) {
        refundService.deleteRefund(orderId);
        return ResponseEntity.ok().build();
    }
}
