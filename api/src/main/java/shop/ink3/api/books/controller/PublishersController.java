package shop.ink3.api.books.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.books.dto.PublisherCreateRequest;
import shop.ink3.api.books.dto.PublisherUpdateRequest;
import shop.ink3.api.books.dto.TagResponse;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.books.dto.PublisherResponse;
import shop.ink3.api.books.service.PublishersService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/publishers")
public class PublishersController {

    private final PublishersService publishersService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<PublisherResponse>>> getPublishers() {
        return ResponseEntity.ok(CommonResponse.success(publishersService.getPublishers()));
    }

    @GetMapping("/{publisherId}")
    public ResponseEntity<CommonResponse<PublisherResponse>> getPublisherById(@PathVariable Long publisherId) {
        return ResponseEntity.ok(CommonResponse.success(publishersService.getPublisherById(publisherId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PublisherResponse>> getPublisherByName(@RequestParam String publisherName) {
        return ResponseEntity.ok(CommonResponse.success(publishersService.getPublisherByName(publisherName)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<PublisherResponse>> createPublisher(@RequestBody PublisherCreateRequest publisherCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(publishersService.createPublisher(publisherCreateRequest)));
    }

    @PutMapping("/{publisherId}")
    public ResponseEntity<CommonResponse<PublisherResponse>> updatePublisher(@PathVariable Long publisherId,
                                                                             @RequestBody PublisherUpdateRequest publisherUpdateRequest) {
        return ResponseEntity.ok(CommonResponse.update(publishersService.updatePublisher(publisherId, publisherUpdateRequest)));
    }

    @DeleteMapping("/{publisherId}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long publisherId) {
        publishersService.deletePublisher(publisherId);
        return ResponseEntity.noContent().build();
    }
}
