package shop.ink3.api.book.book.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.author.exception.AuthorNotFoundException;
import shop.ink3.api.book.author.repository.AuthorRepository;
import shop.ink3.api.book.book.dto.AuthorDto;
import shop.ink3.api.book.book.dto.AuthorRoleRequest;
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.dto.BookRegisterRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.BookUpdateRequest;
import shop.ink3.api.book.book.dto.MainBookResponse;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.exception.BookNotFoundException;
import shop.ink3.api.book.book.exception.DuplicateIsbnException;
import shop.ink3.api.book.book.exception.InvalidCategoryDepthException;
import shop.ink3.api.book.book.exception.InvalidCategorySelectionException;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookResponse;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.exception.CategoryNotFoundException;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.book.publisher.exception.PublisherNotFoundException;
import shop.ink3.api.book.publisher.repository.PublisherRepository;
import shop.ink3.api.book.tag.entity.Tag;
import shop.ink3.api.book.tag.exception.TagNotFoundException;
import shop.ink3.api.book.tag.repository.TagRepository;
import shop.ink3.api.common.dto.PageResponse;

@Transactional
@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final TagRepository tagRepository;

    /**
     * Retrieves a book by its ID.
     *
     * @param bookId the unique identifier of the book to retrieve
     * @return the book details as a BookResponse
     * @throws BookNotFoundException if no book with the given ID exists
     */
    @Transactional(readOnly = true)
    public BookResponse getBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        return BookResponse.from(book);
    }

    /**
     * Retrieves a paginated list of all books.
     *
     * @param pageable pagination and sorting information
     * @return a paginated response containing book details
     */
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return PageResponse.from(books.map(BookResponse::from));
    }

    /**
     * Retrieves the top 5 best-selling books.
     *
     * @return a page response containing up to 5 best-selling books as MainBookResponse DTOs
     */
    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getTop5BestSellerBooks() {
        Page<Book> top5BestSellerBooks = bookRepository.findBestSellerBooks(Pageable.ofSize(5));
        return PageResponse.from(top5BestSellerBooks.map(MainBookResponse::from));
    }

    /**
     * Retrieves a paginated list of best-selling books.
     *
     * @param pageable pagination and sorting information
     * @return a paginated response containing best-selling books as MainBookResponse DTOs
     */
    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getAllBestSellerBooks(Pageable pageable) {
        Page<Book> bestSellerBooks = bookRepository.findBestSellerBooks(pageable);
        return PageResponse.from(bestSellerBooks.map(MainBookResponse::from));
    }

    /**
     * Retrieves the top 5 newest books ordered by publication date descending.
     *
     * @return a page response containing up to 5 newest books as MainBookResponse DTOs
     */
    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getTop5NewBooks() {
        Page<Book> top5RecommendedBooks = bookRepository.findAllByOrderByPublishedAtDesc(Pageable.ofSize(5));
        return PageResponse.from(top5RecommendedBooks.map(MainBookResponse::from));
    }

    /**
     * Retrieves a paginated list of the newest books, ordered by publication date descending.
     *
     * @param pageable pagination and sorting information
     * @return a paginated response containing the newest books
     */
    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getAllNewBooks(Pageable pageable) {
        Page<Book> bestRecommendedBooks = bookRepository.findAllByOrderByPublishedAtDesc(pageable);
        return PageResponse.from(bestRecommendedBooks.map(MainBookResponse::from));
    }

    /**
     * Retrieves the top 5 recommended books.
     *
     * @return a page response containing up to 5 recommended books mapped to MainBookResponse DTOs
     */
    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getTop5RecommendedBooks() {
        Page<Book> top5RecommendedBooks = bookRepository.findRecommendedBooks(Pageable.ofSize(5));
        return PageResponse.from(top5RecommendedBooks.map(MainBookResponse::from));
    }

    /**
     * Retrieves a paginated list of recommended books.
     *
     * @param pageable pagination and sorting information
     * @return a paginated response containing recommended books
     */
    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getAllRecommendedBooks(Pageable pageable) {
        Page<Book> bestRecommendedBooks = bookRepository.findRecommendedBooks(pageable);
        return PageResponse.from(bestRecommendedBooks.map(MainBookResponse::from));
    }

    /**
     * Creates a new book with the provided details, including categories, authors with roles, publisher, and tags.
     *
     * Validates that at least one and no more than ten categories are selected, and that each category meets minimum depth requirements. Throws exceptions if referenced publisher or authors are not found.
     *
     * @param request the details for the new book, including category IDs, author-role pairs, publisher ID, and tag IDs
     * @return the created book as a BookResponse DTO
     * @throws InvalidCategorySelectionException if the number of categories is invalid
     * @throws InvalidCategoryDepthException if any selected category does not meet minimum depth requirements
     * @throws PublisherNotFoundException if the specified publisher does not exist
     * @throws AuthorNotFoundException if any specified author does not exist
     */
    public BookResponse createBook(BookCreateRequest request) {

        if (request.categoryIds() == null || request.categoryIds().isEmpty()) {
            throw new InvalidCategorySelectionException("최소 한 개 이상의 카테고리를 선택해야 합니다.");
        }

        if (request.categoryIds().size() > 10) {
            throw new InvalidCategorySelectionException("카테고리는 최대 10개까지만 선택할 수 있습니다.");
        }

        Publisher publisher = publisherRepository.findById(request.publisherId()).orElseThrow(() -> new PublisherNotFoundException(request.publisherId()));
        Book book = Book.builder()
                .isbn(request.isbn())
                .title(request.title())
                .contents(request.contents())
                .description(request.description())
                .publishedAt(request.publishedAt())
                .originalPrice(request.originalPrice())
                .salePrice(request.salePrice())
                .quantity(request.quantity())
                .status(request.status())
                .isPackable(request.isPackable())
                .thumbnailUrl(request.thumbnailUrl())
                .publisher(publisher)
                .build();

        List<Category> categories = categoryRepository.findAllById(request.categoryIds());
        validateCategoryDepth(categories);
        for(Category category : categories) {
            book.addBookCategory(category);
        }

        List<AuthorRoleRequest> authors = request.authors();
        for(AuthorRoleRequest authorRoleRequest : authors) {
            Author author = authorRepository.findById(authorRoleRequest.authorId())
                    .orElseThrow(() -> new AuthorNotFoundException(authorRoleRequest.authorId()));
            book.addBookAuthor(author, authorRoleRequest.role());
        }

        List<Tag> tags = tagRepository.findAllById(request.tagIds());
        for(Tag tag : tags) {
            book.addBookTag(tag);
        }

        return BookResponse.from(bookRepository.save(book));
    }

    public BookResponse updateBook(Long bookId, BookUpdateRequest request) {

        if (request.categoryIds() == null || request.categoryIds().isEmpty()) {
            throw new InvalidCategorySelectionException("최소 한 개 이상의 카테고리를 선택해야 합니다.");
        }

        if (request.categoryIds().size() > 10) {
            throw new InvalidCategorySelectionException("카테고리는 최대 10개까지만 선택할 수 있습니다.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        Publisher publisher = publisherRepository.findById(request.publisherId())
                .orElseThrow(() -> new PublisherNotFoundException(request.publisherId()));

        book.updateBook(
                request.isbn(),
                request.title(),
                request.contents(),
                request.description(),
                request.publishedAt(),
                request.originalPrice(),
                request.salePrice(),
                request.quantity(),
                request.status(),
                request.isPackable(),
                request.thumbnailUrl(),
                publisher
        );

        book.getBookCategories().clear();
        book.getBookAuthors().clear();
        book.getBookTags().clear();

        List<Long> categoryIds = request.categoryIds();
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        validateCategoryDepth(categories);

        List<AuthorRoleRequest> authors = request.authors();
        List<Long> tagIds = request.tagIds();

        // 카테고리 다시 설정
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
            book.addBookCategory(category);
        }

        // 작가 다시 설정
        for (AuthorRoleRequest authorRoleRequest : authors) {
            Author author = authorRepository.findById(authorRoleRequest.authorId())
                    .orElseThrow(() -> new AuthorNotFoundException(authorRoleRequest.authorId()));
            book.addBookAuthor(author, authorRoleRequest.role()); // 역할 포함
        }

        // 태그 다시 설정
        for (Long tagId : tagIds) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new TagNotFoundException(tagId));
            book.addBookTag(tag);
        }

        return BookResponse.from(book);
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookId the ID of the book to delete
     * @throws BookNotFoundException if the book with the specified ID does not exist
     */
    public void deleteBook(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException(bookId));

        bookRepository.delete(book);
    }
    /**
     * Registers a new book using data from an Aladin API response and additional request details.
     *
     * Checks for duplicate ISBNs and throws a {@link DuplicateIsbnException} if the ISBN already exists.
     * Finds or creates the publisher and authors as needed, builds the book entity, and associates it with categories and tags.
     * Parses and adds authors with their roles, creates or retrieves the category hierarchy, and attaches tags if provided.
     *
     * @param request the registration request containing Aladin API data and additional book details
     * @return the registered book as a {@link BookResponse}
     * @throws DuplicateIsbnException if a book with the same ISBN already exists
     */

    public BookResponse registerBook(BookRegisterRequest request) {
        AladinBookResponse dto = request.aladinBookResponse();
        if (bookRepository.existsByIsbn(dto.isbn13())) {
            throw new DuplicateIsbnException(dto.isbn13());
        }

        Publisher publisher = publisherRepository.findByName(dto.publisher())
                .orElseGet(() -> publisherRepository.save(Publisher.builder().name(dto.publisher()).build()));

        Book book = Book.builder()
                .title(dto.title())
                .description(dto.description())
                .contents(dto.toc())
                .publisher(publisher)
                .publishedAt(LocalDate.parse(dto.pubDate()))
                .isbn(dto.isbn13())
                .originalPrice(dto.priceStandard())
                .salePrice(request.priceSales())
                .quantity(request.quantity())
                .status(request.status())
                .isPackable(request.isPackable())
                .thumbnailUrl(dto.cover())
                .build();
        bookRepository.save(book); // 먼저 저장해서 ID 확보

        List<AuthorDto> authorDtos = parseAuthors(dto.author());
        for (AuthorDto authorDto : authorDtos) {
            Author author = authorRepository.findByName(authorDto.authorName())
                    .orElseGet(() -> authorRepository.save(Author.builder().name(authorDto.authorName()).build()));
            book.addBookAuthor(author, authorDto.role());
        }

        Category category = createCategoryHierarchy(dto.categoryName());
        book.addBookCategory(category);

        List<Long> tagIds = request.tagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            for (Tag tag : tags) {
                book.addBookTag(tag);
            }
        }

        return BookResponse.from(book);
    }
    /**
     * Validates that each category in the list has a parent category, ensuring a minimum depth of two levels.
     *
     * @param categories the list of categories to validate
     * @throws InvalidCategoryDepthException if any category is a top-level category without a parent
     */

    private void validateCategoryDepth(List<Category> categories) {
        for (Category category : categories) {
            if (category.getParent() == null) {
                throw new InvalidCategoryDepthException();
            }
        }
    }
    /**
     * Parses a comma-separated author string from the Aladin API, extracting author names and their roles.
     *
     * For each author, retrieves the corresponding author entity from the repository. If a role is specified in parentheses, it is assigned to the author; otherwise, the default role "지은이" is used. Throws an exception if any author is not found.
     *
     * @param authorString a comma-separated string of author names, optionally with roles in parentheses (e.g., "홍길동(역자), 김철수")
     * @return a list of AuthorDto objects containing author IDs, names, and roles
     * @throws AuthorNotFoundException if any author name does not exist in the repository
     */

    public List<AuthorDto> parseAuthors(String authorString) {
        List<String> parts = Arrays.stream(authorString.split(","))
                .map(String::trim)
                .toList();

        List<AuthorDto> result = new ArrayList<>(Collections.nCopies(parts.size(), null));

        Pattern pattern = Pattern.compile("^(.*)\\(([^()]+)\\)$");
        String currentRole = null;
        for (int i = parts.size() - 1; i >= 0; i--) {
            String part = parts.get(i);
            Matcher matcher = pattern.matcher(part);
            if (matcher.matches()) {
                String nameWithPossibleParens = matcher.group(1).trim();
                currentRole = matcher.group(2).trim();
                Author author = authorRepository.findByName(nameWithPossibleParens).orElseThrow(() -> new AuthorNotFoundException(nameWithPossibleParens));
                result.set(i, new AuthorDto(author.getId(),nameWithPossibleParens, currentRole));
            } else {
                Author author = authorRepository.findByName(part).orElseThrow(() -> new AuthorNotFoundException(part));
                result.set(i, new AuthorDto(author.getId(), part, currentRole != null ? currentRole : "지은이"));
            }
        }
        return result;
    }
    /**
     * Creates or retrieves a hierarchical category structure from a delimited category path string.
     *
     * Splits the given category path by the ">" delimiter, trims each category name, and for each level,
     * finds an existing category by name or creates a new one with the previous category as its parent.
     *
     * @param categoryPath the delimited category path (e.g., "국내도서>소설/시/희곡/한국소설")
     * @return the deepest category in the created or found hierarchy
     */

    public Category createCategoryHierarchy(String categoryPath) {
        String[] categoryNames = categoryPath.split(">");
        Category parent = null;

        for (String name : categoryNames) {
            name = name.trim();

            // 이름으로 카테고리 존재 여부 확인
            String finalName = name;
            Category finalParent = parent;

            parent = categoryRepository.findByName(name)
                    .orElseGet(() -> {
                        Category newCategory = Category.builder()
                                .name(finalName)
                                .parent(finalParent)
                                .build();
                        return categoryRepository.save(newCategory);
                    });
        }
        return parent;
    }
}
