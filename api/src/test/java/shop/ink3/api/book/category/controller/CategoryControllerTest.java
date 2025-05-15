package shop.ink3.api.book.category.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shop.ink3.api.book.category.dto.CategoryCreateRequest;
import shop.ink3.api.book.category.dto.CategoryUpdateRequest;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.exception.CategoryAlreadyExistsException;
import shop.ink3.api.book.category.exception.CategoryNotFoundException;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.book.category.service.CategoryService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    private CategoryRepository categoryRepository;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void createCategory_shouldCreateNewCategory() {
        CategoryCreateRequest request = new CategoryCreateRequest("컴퓨터", null);
        Category category = Category.builder().id(1L).name("컴퓨터").build();

        when(categoryRepository.existsByName("컴퓨터")).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(category);

        var response = categoryService.createCategory(request);

        assertThat(response.name()).isEqualTo("컴퓨터");
        verify(categoryRepository).save(any());
    }

    @Test
    void createCategory_whenNameExists_shouldThrowException() {
        when(categoryRepository.existsByName("컴퓨터")).thenReturn(true);

        assertThatThrownBy(() ->
                categoryService.createCategory(new CategoryCreateRequest("컴퓨터", null))
        ).isInstanceOf(CategoryAlreadyExistsException.class);
    }

    @Test
    void updateCategory_shouldUpdateSuccessfully() {
        Category existing = Category.builder().id(1L).name("컴퓨터").build();
        CategoryUpdateRequest request = new CategoryUpdateRequest("프로그래밍", null);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

        var result = categoryService.updateCategory(1L, request);

        assertThat(result.name()).isEqualTo("프로그래밍");
    }

    @Test
    void updateCategory_whenNotFound_shouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.updateCategory(1L, new CategoryUpdateRequest("프로그래밍", null))
        ).isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void deleteCategory_shouldDeleteSuccessfully() {
        Category category = Category.builder().id(1L).name("컴퓨터").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_whenNotFound_shouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void getCategoryById_shouldReturnCategory() {
        Category category = Category.builder().id(1L).name("컴퓨터").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        var result = categoryService.getCategoryById(1L);
        assertThat(result.name()).isEqualTo("컴퓨터");
    }

    @Test
    void getCategoryTree_shouldBuildTree() {
        Category parent = Category.builder().id(1L).name("컴퓨터").build();
        Category child = Category.builder().id(2L).name("프로그래밍").category(parent).build();

        when(categoryRepository.findAll()).thenReturn(List.of(parent, child));

        var tree = categoryService.getCategoryTree();
        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).children()).hasSize(1);
        assertThat(tree.get(0).children().get(0).name()).isEqualTo("프로그래밍");
    }

    @Test
    void getAllCategories_shouldReturnList() {
        Category category1 = Category.builder().id(1L).name("컴퓨터").build();
        Category category2 = Category.builder().id(2L).name("디자인").build();

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        var result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("컴퓨터");
        assertThat(result.get(1).name()).isEqualTo("디자인");
    }

    @Test
    void updateCategory_shouldUpdateWithParent() {
        Category parent = Category.builder().id(99L).name("개발").build();
        Category existing = Category.builder().id(1L).name("컴퓨터").build();

        CategoryUpdateRequest request = new CategoryUpdateRequest("프로그래밍", 99L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(99L)).thenReturn(Optional.of(parent));

        var result = categoryService.updateCategory(1L, request);

        assertThat(result.name()).isEqualTo("프로그래밍");
        assertThat(result.parentId()).isEqualTo(99L);
    }

    @Test
    void updateCategory_whenParentIdNotFound_shouldThrowException() {
        Category existing = Category.builder().id(1L).name("컴퓨터").build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.updateCategory(1L, new CategoryUpdateRequest("프로그래밍", 99L))
        ).isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void createCategory_whenParentIdNotFound_shouldThrowException() {
        CategoryCreateRequest request = new CategoryCreateRequest("프론트엔드", 99L);

        when(categoryRepository.existsByName("프론트엔드")).thenReturn(false);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}