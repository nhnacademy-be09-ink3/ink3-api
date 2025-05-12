package shop.ink3.api.book.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.category.dto.CategoryCreateRequest;
import shop.ink3.api.book.category.dto.CategoryResponse;
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
            // throw new CategoryAlreadyExistsException(categoryName);
        }

        Category category = Category.builder().name(categoryCreateRequest.name()).build();
        return CategoryResponse.from(categoryRepository.save(category));
    }
}
