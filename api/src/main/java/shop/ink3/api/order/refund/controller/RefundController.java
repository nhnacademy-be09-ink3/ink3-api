package shop.ink3.api.order.refund.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.order.service.OrderMainService;
import shop.ink3.api.order.refund.dto.RefundCreateRequest;
import shop.ink3.api.order.refund.dto.RefundResponse;
import shop.ink3.api.order.refund.dto.RefundUpdateRequest;
import shop.ink3.api.order.refund.service.RefundService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/refunds")
public class RefundController {
    private final RefundService refundService;
    private final OrderMainService orderMainService;

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<RefundResponse>>> getRefunds(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<RefundResponse> allRefundList = refundService.getAllRefundList(pageable);
        return ResponseEntity.ok(CommonResponse.success(allRefundList));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<RefundResponse>> getRefund(
            @PathVariable long orderId) {
        return ResponseEntity.ok(CommonResponse.success(refundService.getOrderRefund(orderId)));
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<PageResponse<RefundResponse>>> getUserRefundList(
            HttpServletRequest request, Pageable pageable) {
        long userId = Long.parseLong(request.getHeader("X-User-Id"));
        return ResponseEntity.ok(CommonResponse.success(refundService.getUserRefundList(userId, pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<RefundResponse>> createRefund(
            @RequestBody RefundCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(orderMainService.createRefund(request)));
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<Void> approveRefund(
            @PathVariable long orderId,
            @RequestHeader("X-User-Id") long userId) {
        orderMainService.approveRefund(userId, orderId);
        return ResponseEntity.ok().build();
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
