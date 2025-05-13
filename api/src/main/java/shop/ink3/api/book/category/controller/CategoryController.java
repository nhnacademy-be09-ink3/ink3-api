package shop.ink3.api.book.category.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.book.category.dto.CategoryCreateRequest;
import shop.ink3.api.book.category.dto.CategoryResponse;
import shop.ink3.api.book.category.dto.CategoryUpdateRequest;
import shop.ink3.api.book.category.service.CategoryService;
import shop.ink3.api.common.dto.CommonResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(
            @RequestBody CategoryCreateRequest categoryCreateRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(categoryService.createCategory(categoryCreateRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest) {
        return ResponseEntity.ok(
                CommonResponse.success(categoryService.updateCategory(id, categoryUpdateRequest))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponse>> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(
                CommonResponse.success(categoryService.getCategoryById(id))
        );
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(
                CommonResponse.success(categoryService.getAllCategories())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tree")
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getCategoryTree() {
        return ResponseEntity.ok(CommonResponse.success(categoryService.getCategoryTree()));
    }


}
