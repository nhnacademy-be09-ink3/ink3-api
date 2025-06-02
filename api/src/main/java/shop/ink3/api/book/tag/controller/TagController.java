package shop.ink3.api.book.tag.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.book.tag.dto.TagCreateRequest;
import shop.ink3.api.book.tag.dto.TagResponse;
import shop.ink3.api.book.tag.dto.TagUpdateRequest;
import shop.ink3.api.book.tag.service.TagService;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tags")
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<TagResponse>>> getTags(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(tagService.getTags(pageable)));
    }

    /**
     * Retrieves a tag by its ID or name.
     *
     * If the `id` parameter is provided, returns the tag with the specified ID. If the `name` parameter is provided, returns the tag with the specified name. Throws an exception if neither parameter is provided.
     *
     * @param id the unique identifier of the tag (optional)
     * @param name the name of the tag (optional)
     * @return a response containing the tag details
     * @throws IllegalArgumentException if neither `id` nor `name` is provided
     */
    @GetMapping("/detail")
    public ResponseEntity<CommonResponse<TagResponse>> getTag(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name) {

        if (id != null) {
            return ResponseEntity.ok(CommonResponse.success(tagService.getTagById(id)));
        } else if (name != null) {
            return ResponseEntity.ok(CommonResponse.success(tagService.getTagByName(name)));
        } else {
            throw new IllegalArgumentException("Either id or name must be provided");
        }
    }

    /**
     * Creates a new tag using the provided request data.
     *
     * @param tagCreateRequest the validated request body containing tag creation details
     * @return a response entity with the created tag wrapped in a common response and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<CommonResponse<TagResponse>> createTag(@RequestBody @Valid TagCreateRequest tagCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(tagService.createTag(tagCreateRequest)));
    }

    /**
     * Updates an existing tag with the provided information.
     *
     * @param tagId the ID of the tag to update
     * @param request the updated tag data
     * @return the updated tag wrapped in a common response
     */
    @PutMapping("/{tagId}")
    public ResponseEntity<CommonResponse<TagResponse>> updateTag(
            @PathVariable Long tagId,
            @RequestBody @Valid TagUpdateRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.success(tagService.updateTag(tagId, request)));
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
