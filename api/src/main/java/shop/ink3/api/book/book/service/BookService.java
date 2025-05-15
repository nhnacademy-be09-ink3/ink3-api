package shop.ink3.api.book.book.service;

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
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.BookUpdateRequest;
import shop.ink3.api.book.book.dto.BookSearchRequest;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.exception.BookNotFoundException;
import shop.ink3.api.book.book.enums.BookSortType;
import shop.ink3.api.book.book.exception.DuplicateIsbnException;
import shop.ink3.api.book.book.external.aladin.AladinClient;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookDto;
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
    private final AladinClient aladinClient;

    public BookResponse createBook(BookCreateRequest request) {
        Publisher publisher = publisherRepository.findById(request.publisherId()).orElseThrow(() -> new PublisherNotFoundException(request.publisherId()));
        Book book = Book.builder()
                .ISBN(request.ISBN())
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
        for(Category category : categories) {
            book.addBookCategory(category);
        }

        List<Author> authors = authorRepository.findAllById(request.authorIds());
        for(Author author : authors) {
            book.addBookAuthor(author);
        }

        List<Tag> tags = tagRepository.findAllById(request.tagIds());
        for(Tag tag : tags) {
            book.addBookTag(tag);
        }

        return BookResponse.from(bookRepository.save(book));
    }

    // 알라딘 ISBN 기반으로 도서 등록
    public BookResponse registerBookByIsbn(String isbn13) {
        // 중복 ISBN 체크
        if (bookRepository.existsByISBN(isbn13)) {
            throw new DuplicateIsbnException(isbn13);
        }

        AladinBookDto dto = aladinClient.fetchBookByIsbn(isbn13);

        Publisher publisher = publisherRepository.findByName(dto.publisher())
                .orElseGet(() -> publisherRepository.save(Publisher.builder().name(dto.publisher()).build()));

        // 저자 저장 또는 조회
        Author author = authorRepository.findByName(dto.author())
                .orElseGet(() -> authorRepository.save(Author.builder().name(dto.author()).build()));

        // 도서 객체 생성
        Book book = Book.builder()
                .title(dto.title())
                .description(dto.description())
                .contents(dto.toc())
                .publisher(publisher)
                .publishedAt(LocalDate.parse(dto.pubDate()))
                .ISBN(dto.isbn13())
                .originalPrice(dto.priceStandard())
                .salePrice(dto.priceSales())
                .thumbnailUrl(dto.cover())
                .isPackable(true)
                .status(BookStatus.AVAILABLE)
                .build();

        // 저자 연결
        book.addBookAuthor(author);

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

    public BookResponse updateBook(Long bookId, BookUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        Publisher publisher = publisherRepository.findById(request.publisherId())
                .orElseThrow(() -> new PublisherNotFoundException(request.publisherId()));

        book.updateBook(
                request.ISBN(),
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
        List<Long> authorIds = request.authorIds();
        List<Long> tagIds = request.tagIds();

        // 카테고리 다시 설정
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
            book.addBookCategory(category);
        }

        // 작가 다시 설정
        for (Long authorId : authorIds) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new AuthorNotFoundException(authorId));
            book.addBookAuthor(author);
        }

        // 태그 다시 설정
        for (Long tagId : tagIds) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new TagNotFoundException(tagId));
            book.addBookTag(tag);
        }

        return BookResponse.from(book);
        //return BookResponse.from(bookRepository.save(book));
    }

    public void deleteBook(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException(bookId));

        bookRepository.delete(book);
    }

    // 제목 기반 검색
    public List<BookResponse> findAllByTitle(String title) {
        return bookRepository.getBooksByTitle(title).stream()
                .map(BookResponse::from)
                .toList();
    }

    // 단건 조회
    public BookResponse findById(Long id) {
        return bookRepository.findById(id)
                .map(BookResponse::from)
                .orElse(null);
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