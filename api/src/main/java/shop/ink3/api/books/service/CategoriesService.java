package shop.ink3.api.books.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.books.dto.CategoryCreateRequest;
import shop.ink3.api.books.dto.CategoryResponse;
import shop.ink3.api.books.categories.entity.Categories;
import shop.ink3.api.books.repository.CategoriesRepository;

@RequiredArgsConstructor
@Service
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;

    /*
    부모 카테고리가 없는 카테고리 생성
    부모 카테고리가 있는 카테고리 생성
    부모 카테고리가 없는 카테고리의 카테고리 수정
     */


    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        String categoryName = categoryCreateRequest.name();
        if (categoriesRepository.existsByName(categoryName)) {
            // throw new CategoryAlreadyExistsException(categoryName);
        }

        Categories categories = Categories.builder().name(categoryCreateRequest.name()).build();
        return CategoryResponse.from(categoriesRepository.save(categories));
    }
}
