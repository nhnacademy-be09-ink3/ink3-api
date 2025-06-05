package shop.ink3.api.order.guest.controller;


import lombok.RequiredArgsConstructor;
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
import shop.ink3.api.order.guest.dto.GuestCreateRequest;
import shop.ink3.api.order.guest.dto.GuestResponse;
import shop.ink3.api.order.guest.service.GuestOrderMainService;
import shop.ink3.api.order.guest.service.GuestService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/guests")
public class GuestController {
    private final GuestService guestService;

    // 비회원 조회
    @GetMapping
    public ResponseEntity<CommonResponse<GuestResponse>> getGuest(
            @RequestParam long orderId) {
        return ResponseEntity
                .ok(CommonResponse.success(guestService.getGuestByOrderId(orderId)));
    }


    // 비회원 삭제
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<CommonResponse<Void>> deleteGuestByOrderId(@PathVariable long orderId) {
        guestService.deleteGuestOrderByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }
}
