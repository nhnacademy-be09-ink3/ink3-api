package shop.ink3.api.book.category.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.category.dto.CategoryCreateRequest;
import shop.ink3.api.book.category.dto.CategoryResponse;
import shop.ink3.api.book.category.dto.CategoryUpdateRequest;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.exception.CategoryAlreadyExistsException;
import shop.ink3.api.book.category.exception.CategoryNotFoundException;
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
            throw new CategoryAlreadyExistsException(categoryName);
        }

        Category category = Category.builder()
                .name(categoryCreateRequest.name())
                .build();

        if (categoryCreateRequest.parentId() != null) {
            Category parent = categoryRepository.findById(categoryCreateRequest.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(categoryCreateRequest.parentId()));
            category.updateCategory(parent);
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다: " + categoryCreateRequest.parentId()));
            category.updateParentCategory(parent);
        }

        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));
        category.updateCategoryName(categoryUpdateRequest.name());

        if (categoryUpdateRequest.parentId() != null) {
            Category parent = categoryRepository.findById(categoryUpdateRequest.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다: " + categoryUpdateRequest.parentId()));
            category.updateParentCategory(parent);
        } else {
            category.updateParentCategory(null);
        }

        return CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryResponse.from(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        List<Category> categories = categoryRepository.findAll();

        Map<Long, CategoryResponse> idToDto = new HashMap<>();
        for (Category category : categories) {
            idToDto.put(category.getId(), CategoryResponse.from(category));
        }

        for (Category category : categories) {
            if (category.getCategory() != null) {
                Long parentId = category.getCategory().getId();
                CategoryResponse parentDto = idToDto.get(parentId);
                CategoryResponse childDto = idToDto.get(category.getId());
                parentDto.children().add(childDto);
            }
        }

        return categories.stream()
                .filter(c -> c.getCategory() == null)
                .map(c -> idToDto.get(c.getId()))
                .toList();
    }
}