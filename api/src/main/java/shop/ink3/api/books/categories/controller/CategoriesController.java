package shop.ink3.api.books.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.books.categories.dto.CategoryCreateRequest;
import shop.ink3.api.books.categories.dto.CategoryResponse;
import shop.ink3.api.books.categories.service.CategoriesService;
import shop.ink3.api.common.dto.CommonResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books/categories")
public class CategoriesController {
    private final CategoriesService categoriesService;

    @PostMapping
    public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(@RequestBody CategoryCreateRequest categoryCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(categoriesService.createCategory(categoryCreateRequest)));
    }
}
