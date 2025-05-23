package shop.ink3.api.book.category.controller;

import jakarta.validation.Valid;
import java.util.List;
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
import shop.ink3.api.book.category.dto.CategoryCreateRequest;
import shop.ink3.api.book.category.dto.CategoryResponse;
import shop.ink3.api.book.category.dto.CategoryUpdateRequest;
import shop.ink3.api.book.category.service.CategoryService;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(@RequestBody CategoryCreateRequest categoryCreateRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(categoryService.createCategory(categoryCreateRequest)));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CommonResponse<CategoryResponse>> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest) {
        return ResponseEntity.ok(
                CommonResponse.success(categoryService.updateCategory(categoryId, categoryUpdateRequest))
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CommonResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
                CommonResponse.success(categoryService.getCategoryById(categoryId))
        );
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<CategoryResponse>>> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok(
                CommonResponse.success(categoryService.getAllCategories(pageable))
        );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tree")
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getCategoryTree() {
        return ResponseEntity.ok(CommonResponse.success(categoryService.getCategoryTree()));
    }
}
