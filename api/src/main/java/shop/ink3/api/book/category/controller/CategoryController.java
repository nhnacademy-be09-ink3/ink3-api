package shop.ink3.api.book.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.book.category.dto.CategoryCreateRequest;
import shop.ink3.api.book.category.dto.CategoryResponse;
import shop.ink3.api.book.category.service.CategoryService;
import shop.ink3.api.common.dto.CommonResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(@RequestBody CategoryCreateRequest categoryCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(categoryService.createCategory(categoryCreateRequest)));
    }
}
