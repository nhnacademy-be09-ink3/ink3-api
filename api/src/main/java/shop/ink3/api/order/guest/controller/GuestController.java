package shop.ink3.api.order.guest.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.guest.dto.GuestOrderCreateRequest;
import shop.ink3.api.order.guest.dto.GuestOrderResponse;
import shop.ink3.api.order.guest.service.GuestOrderService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/guests")
public class GuestController {

    private final GuestOrderService guestOrderService;

    @GetMapping("/{guestId}")
    public ResponseEntity<CommonResponse<GuestOrderResponse>> getGuestOrder(@PathVariable long guestId) {
        return ResponseEntity
                .ok(CommonResponse.success(guestOrderService.getGuestOrder(guestId)));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<CommonResponse<GuestOrderResponse>> getGuestOrderByOrderId(@PathVariable long orderId) {
        return ResponseEntity
                .ok(CommonResponse.success(guestOrderService.getGuestOrderByOrderId(orderId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<GuestOrderResponse>>> getGuestOrderList(
            @RequestParam String email,
            @RequestParam String password,
            Pageable pageable) {
        return ResponseEntity
                .ok(CommonResponse.success(guestOrderService.getGuestOrderList(email, password, pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<GuestOrderResponse>> createGuestOrder(
            @RequestBody GuestOrderCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(guestOrderService.createGuestOrder(request)));
    }

    @DeleteMapping("/{guestId}")
    public ResponseEntity<CommonResponse<Void>> deleteGuestOrder(@PathVariable long guestId) {
        guestOrderService.deleteGuestOrder(guestId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<CommonResponse<Void>> deleteGuestOrderByOrderId(@PathVariable long orderId) {
        guestOrderService.deleteGuestOrderByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }
}
