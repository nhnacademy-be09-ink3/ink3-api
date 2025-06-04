package shop.ink3.api.book.category.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.category.dto.CategoryCreateRequest;
import shop.ink3.api.book.category.dto.CategoryResponse;
import shop.ink3.api.book.category.dto.CategoryUpdateRequest;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.exception.CategoryAlreadyExistsException;
import shop.ink3.api.book.category.exception.CategoryNotFoundException;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.common.dto.PageResponse;

@Transactional
@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

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
            category.updateParentCategory(parent);
        }

        return CategoryResponse.from(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.updateCategoryName(categoryUpdateRequest.name());

        if (categoryUpdateRequest.parentId() != null) {
            Category parent = categoryRepository.findById(categoryUpdateRequest.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(categoryUpdateRequest.parentId()));
            category.updateParentCategory(parent);
        } else {
            category.updateParentCategory(null);
        }

        return CategoryResponse.from(category);
    }


    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (categoryRepository.existsByParent(category)) {
            throw new IllegalStateException("하위 카테고리가 존재하여 삭제할 수 없습니다.");
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryResponse.from(category);
    }

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return PageResponse.from(categories.map(CategoryResponse::from));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        List<Category> categories = categoryRepository.findAll();

        Map<Long, CategoryResponse> idToDto = new HashMap<>();
        for (Category category : categories) {
            idToDto.put(category.getId(), CategoryResponse.from(category));
        }

        for (Category category : categories) {
            if (category.getParent() != null) {
                Long parentId = category.getParent().getId();
                CategoryResponse parentDto = idToDto.get(parentId);
                CategoryResponse childDto = idToDto.get(category.getId());
                parentDto.children().add(childDto);
            }
        }

        return categories.stream()
                .filter(c -> c.getParent() == null)
                .map(c -> idToDto.get(c.getId()))
                .toList();
    }

    private void validateNoCircularReference(Category category, Category parent) {
        Category current = parent;
        while (current != null) {
            if (current.getId() != null && current.getId().equals(category.getId())) {
                throw new IllegalArgumentException("자기 자신 또는 하위 카테고리를 부모로 설정할 수 없습니다.");
            }
            current = current.getParent();
        }
    }

}