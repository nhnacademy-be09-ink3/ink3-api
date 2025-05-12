package shop.ink3.api.books.controller;

import java.util.List;
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
import shop.ink3.api.books.dto.TagCreateRequest;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.books.dto.TagResponse;
import shop.ink3.api.books.service.TagsService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books/tags")
public class TagsController {

    private final TagsService tagsService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<TagResponse>>> getTags() {
        return ResponseEntity.ok(CommonResponse.success(tagsService.getTags()));
    }

    @GetMapping("/tagId/{tagId}")
    public ResponseEntity<CommonResponse<TagResponse>> getTagById(@PathVariable Long tagId) {
        return ResponseEntity.ok(CommonResponse.success(tagsService.getTagById(tagId)));
    }

    @GetMapping("/tagName/{tagName}")
    public ResponseEntity<CommonResponse<TagResponse>> getTagByName(@PathVariable String tagName) {
        return ResponseEntity.ok(CommonResponse.success(tagsService.getTagByName(tagName)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<TagResponse>> createTag(@RequestBody TagCreateRequest tagCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(tagsService.createTag(tagCreateRequest)));
    }

    //update

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        tagsService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
