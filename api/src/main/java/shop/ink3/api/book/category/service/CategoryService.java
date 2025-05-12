package shop.ink3.api.book.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.category.dto.CategoryCreateRequest;
import shop.ink3.api.book.category.dto.CategoryResponse;
import shop.ink3.api.book.category.dto.CategoryUpdateRequest;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.repository.CategoryRepository;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /*
    부모 카테고리가 없는 카테고리 생성
    부모 카테고리가 있는 카테고리 생성
    부모 카테고리가 없는 카테고리의 카테고리 수정
     */

    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        String categoryName = categoryCreateRequest.name();

        if (categoryRepository.existsByName(categoryName)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다: " + categoryName);
        }

        Category category = Category.builder()
                .name(categoryCreateRequest.name())
                .build();

        if (categoryCreateRequest.parentId() != null) {
            Category parent = categoryRepository.findById(categoryCreateRequest.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다: " + categoryCreateRequest.parentId()));
            category.setCategory(parent);
        }

        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));

        category.setName(categoryUpdateRequest.name());

        if (categoryUpdateRequest.parentId() != null) {
            Category parent = categoryRepository.findById(categoryUpdateRequest.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다: " + categoryUpdateRequest.parentId()));
            category.setCategory(parent);
        } else {
            category.setCategory(null);
        }

        return CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));
        return CategoryResponse.from(category);
    }
}
