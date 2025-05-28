package shop.ink3.api.order.packaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.packaging.dto.PackagingCreateRequest;
import shop.ink3.api.order.packaging.dto.PackagingResponse;
import shop.ink3.api.order.packaging.dto.PackagingUpdateRequest;
import shop.ink3.api.order.packaging.service.PackagingService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/packagings")
public class PackagingController {

    private final PackagingService packagingService;

    @GetMapping("/{packagingId}")
    public ResponseEntity<CommonResponse<PackagingResponse>> getPackaging(@PathVariable long packagingId) {
        return ResponseEntity
                .ok(CommonResponse.success(packagingService.getPackaging(packagingId)));
    }

    @GetMapping("/activate")
    public ResponseEntity<CommonResponse<PageResponse<PackagingResponse>>> getAvailablePackagingList(
            Pageable pageable) {
        return ResponseEntity
                .ok(CommonResponse.success(packagingService.getAvailablePackagingList(pageable)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<PackagingResponse>>> getPackagingList(Pageable pageable) {
        return ResponseEntity
                .ok(CommonResponse.success(packagingService.getPackagingList(pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<PackagingResponse>> createPackaging(
            @RequestBody PackagingCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(packagingService.createPackaging(request)));
    }

    @PutMapping("/{packagingId}")
    public ResponseEntity<CommonResponse<PackagingResponse>> updatePackaging(
            @PathVariable long packagingId,
            @RequestBody PackagingUpdateRequest request) {
        return ResponseEntity
                .ok(CommonResponse.update(packagingService.updatePackaging(packagingId, request)));
    }

    @PatchMapping("/{packagingId}/activate")
    public ResponseEntity<Void> activatePackaging(@PathVariable long packagingId) {
        packagingService.activate(packagingId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{packagingId}/deactivate")
    public ResponseEntity<Void> deactivatePackaging(@PathVariable long packagingId) {
        packagingService.deactivate(packagingId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{packagingId}")
    public ResponseEntity<Void> deletePackaging(@PathVariable long packagingId) {
        packagingService.deletePackaging(packagingId);
        return ResponseEntity.ok().build();
    }
}
