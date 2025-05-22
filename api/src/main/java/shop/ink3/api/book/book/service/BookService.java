package shop.ink3.api.book.book.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import shop.ink3.api.book.book.dto.BookSearchRequest;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.exception.BookNotFoundException;
import shop.ink3.api.book.book.enums.BookSortType;
import shop.ink3.api.book.book.exception.DuplicateIsbnException;
import shop.ink3.api.book.book.exception.InvalidCategoryDepthException;
import shop.ink3.api.book.book.exception.InvalidCategorySelectionException;
import shop.ink3.api.book.book.external.aladin.AladinClient;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookResponse;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.bookAuthor.entity.BookAuthor;
import shop.ink3.api.book.bookCategory.entity.BookCategory;
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
    private final AladinClient aladinClient;

    // 단건 조회
    @Transactional(readOnly = true)
    public BookResponse getBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        return BookResponse.from(book);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return PageResponse.from(books.map(BookResponse::from));
    }

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

    public void deleteBook(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException(bookId));

        bookRepository.delete(book);
    }

    // 알라딘 ISBN 기반으로 도서 등록
    public BookResponse registerBookByIsbn(String isbn13) {
        // 중복 ISBN 체크
        if (bookRepository.existsByIsbn(isbn13)) {
            throw new DuplicateIsbnException(isbn13);
        }

        AladinBookResponse dto = aladinClient.fetchBookByIsbn(isbn13);

        Publisher publisher = publisherRepository.findByName(dto.publisher())
                .orElseGet(() -> publisherRepository.save(Publisher.builder().name(dto.publisher()).build()));

        // 도서 객체 생성
        Book book = Book.builder()
                .title(dto.title())
                .description(dto.description())
                .contents(dto.toc())
                .publisher(publisher)
                .publishedAt(LocalDate.parse(dto.pubDate()))
                .isbn(dto.isbn13())
                .originalPrice(dto.priceStandard())
                .thumbnailUrl(dto.cover())
                .isPackable(true)
                .status(BookStatus.AVAILABLE)
                .build();

        List<AuthorDto> authorDtos = parseAuthors(dto.author());
        for (AuthorDto authorDto : authorDtos) {
            Author author = authorRepository.findByName(authorDto.name())
                    .orElseGet(() -> authorRepository.save(Author.builder().name(authorDto.name()).build()));
            book.addBookAuthor(author, authorDto.role());
        }

        // 카테고리 파싱 및 연결
        String rawCategoryName = dto.categoryName();
        String finalCategoryName = rawCategoryName;
        if (rawCategoryName != null && rawCategoryName.contains(">")) {
            String[] parts = rawCategoryName.split(">");
            finalCategoryName = parts[parts.length - 1].trim();
        }

        if (finalCategoryName != null && !finalCategoryName.isBlank()) {
            String categoryNameToUse = finalCategoryName;
            Category category = categoryRepository.findByName(categoryNameToUse)
                    .orElseGet(() -> categoryRepository.save(Category.builder().name(categoryNameToUse).build()));
            book.addBookCategory(category);
        }

        // 자동 태그 생성 및 연결 (제목, 설명 기반)
        List<String> keywords = extractKeywords(dto.title(), dto.description());
        for (String keyword : keywords) {
            Tag tag = tagRepository.findByName(keyword)
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(keyword).build()));
            book.addBookTag(tag);
        }

        return BookResponse.from(bookRepository.save(book));
    }

    // 알라딘 api + 자체적으로 조정할 내용 입력하여 도서 등록
    public BookResponse registerBook(BookRegisterRequest request) {
        AladinBookResponse dto = request.aladinBookResponse();

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
            Author author = authorRepository.findByName(authorDto.name())
                    .orElseGet(() -> authorRepository.save(Author.builder().name(authorDto.name()).build()));
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

    // 텍스트에서 키워드 추출 (태그용)
    private List<String> extractKeywords(String... texts) {
        Set<String> result = new LinkedHashSet<>();
        for (String text : texts) {
            if (text == null) continue;
            String[] words = text.split("[\\s,./()\\[\\]\\-]+");
            for (String word : words) {
                if (word.length() >= 2 && word.length() <= 15) {
                    result.add(word.toLowerCase());
                }
                if (result.size() >= 5) break;
            }
            if (result.size() >= 5) break;
        }
        return result.stream().toList();
    }

    // 카테고리는 최소 2단계
    private void validateCategoryDepth(List<Category> categories) {
        for (Category category : categories) {
            if (category.getParent() == null) {
                throw new InvalidCategoryDepthException();
            }
        }
    }

    // 알라딘 API로 가져온 작가 이름 파싱
    public static List<AuthorDto> parseAuthors(String authorString) {
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
                result.set(i, new AuthorDto(nameWithPossibleParens, currentRole));
            } else {
                result.set(i, new AuthorDto(part, currentRole != null ? currentRole : "지은이"));
            }
        }
        return result;
    }

    // 알라딘 api에서 가져온 카테고리 이름 분리 > 계층 구조 생성 (국내도서>소설/시/희곡/한국소설)
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





    /*
     * 검색은 따로 작성
     */

    // 제목 기반 검색
    public List<BookResponse> findAllByTitle(String title) {
        return bookRepository.getBooksByTitle(title).stream()
                .map(BookResponse::from)
                .toList();
    }

    // 저자 이름으로 검색
    public List<BookResponse> findAllByAuthor(String author) {
        return bookRepository.findDistinctByBookAuthorsAuthorNameContainingIgnoreCase(author).stream()
                .map(BookResponse::from)
                .toList();
    }

    public PageResponse<BookResponse> searchBooks(BookSearchRequest request) {
        String title = request.title();
        String author = request.author();
        BookSortType sortType = request.sort();
        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null ? request.size() : 10;

        //정렬조건
        Sort sortOption = switch (sortType) {
            case TITLE_ASC -> Sort.by("title").ascending();
            case PUBLISHED_AT_DESC -> Sort.by("publishedAt").descending();
            case PRICE_ASC -> Sort.by("originalPrice").ascending();
            case PRICE_DESC -> Sort.by("originalPrice").descending();
            default -> Sort.unsorted();
        };

        Pageable pageable = PageRequest.of(page, size, sortOption);

        Page<Book> bookPage;
        if (title != null && !title.isBlank()) {
            bookPage = bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (author != null && !author.isBlank()) {
            bookPage = bookRepository.findDistinctByBookAuthorsAuthorNameContainingIgnoreCase(author, pageable);
        } else {
            bookPage = bookRepository.findAll(pageable);
        }

        return PageResponse.from(bookPage.map(BookResponse::from));
    }
}