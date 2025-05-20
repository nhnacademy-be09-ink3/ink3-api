package shop.ink3.api.order.guestOrder.controller;


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
import shop.ink3.api.order.guestOrder.dto.GuestOrderCreateRequest;
import shop.ink3.api.order.guestOrder.dto.GuestOrderResponse;
import shop.ink3.api.order.guestOrder.service.GuestOrderService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/guestOders")
public class GuestOrderController {

    private final GuestOrderService guestOrderService;

    @GetMapping("/{guestOrderId}")
    public ResponseEntity<CommonResponse<GuestOrderResponse>> getGuestOrder(@PathVariable long guestOrderId){
        return ResponseEntity
                .ok(CommonResponse.success(guestOrderService.getGuestOrder(guestOrderId)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<CommonResponse<GuestOrderResponse>> getGuestOrderByOrderId(@PathVariable long orderId){
        return ResponseEntity
                .ok(CommonResponse.success(guestOrderService.getGuestOrderByOrderId(orderId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<GuestOrderResponse>>> getGuestOrderList(
            @RequestParam String email,
            @RequestParam String password,
            Pageable pageable){
        return ResponseEntity
                .ok(CommonResponse.success(guestOrderService.getGuestOrderList(email, password, pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<GuestOrderResponse>> createGuestOrder(@RequestBody GuestOrderCreateRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(guestOrderService.createGuestOrder(request)));
    }

    @DeleteMapping("/{guestOrderId}")
    public ResponseEntity<CommonResponse<Void>> deleteGuestOrder(@PathVariable long guestOrderId){
        guestOrderService.deleteGuestOrder(guestOrderId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<CommonResponse<Void>> deleteGuestOrderByOrderId(@PathVariable long orderId){
        guestOrderService.deleteGuestOrderByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }
}
