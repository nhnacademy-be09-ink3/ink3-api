package shop.ink3.api.book.book.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
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
import shop.ink3.api.book.bookCategory.entity.BookCategory;
import shop.ink3.api.book.category.dto.CategoryResponse;
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
import shop.ink3.api.common.uploader.MinioUploader;
import shop.ink3.api.review.review.repository.ReviewRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final TagRepository tagRepository;
    private final MinioUploader minioUploader;

    @Value("${minio.book-bucket}")
    private String bucket;

    // 단건 조회
    @Transactional(readOnly = true)
    public BookResponse getBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        double averageRating = getAverageRating(bookId);

        return BookResponse.from(book, averageRating);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookWithCategory(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        List<Category> categories = new ArrayList<>();
        List<Category> list = book.getBookCategories()
                .stream()
                .map(BookCategory::getCategory)
                .toList();
        for(Category category:list) {
            categories.addAll(categoryRepository.findAllAncestors(category.getId()));
        }
        return BookResponse.from(book, categories.stream().map(CategoryResponse::from).toList());
    }

    // 전체 조회

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return PageResponse.from(books.map(BookResponse::from));
    }
    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getTop5BestSellerBooks() {
        Page<Book> top5BestSellerBooks = bookRepository.findBestSellerBooks(Pageable.ofSize(5));
        return PageResponse.from(top5BestSellerBooks.map(MainBookResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getAllBestSellerBooks(Pageable pageable) {
        Page<Book> bestSellerBooks = bookRepository.findBestSellerBooks(pageable);
        return PageResponse.from(bestSellerBooks.map(MainBookResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getTop5NewBooks() {
        Page<Book> top5RecommendedBooks = bookRepository.findAllByOrderByPublishedAtDesc(Pageable.ofSize(5));
        return PageResponse.from(top5RecommendedBooks.map(MainBookResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getAllNewBooks(Pageable pageable) {
        Page<Book> bestRecommendedBooks = bookRepository.findAllByOrderByPublishedAtDesc(pageable);
        return PageResponse.from(bestRecommendedBooks.map(MainBookResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getTop5RecommendedBooks() {
        Page<Book> top5RecommendedBooks = bookRepository.findRecommendedBooks(Pageable.ofSize(5));
        return PageResponse.from(top5RecommendedBooks.map(MainBookResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getAllRecommendedBooks(Pageable pageable) {
        Page<Book> bestRecommendedBooks = bookRepository.findRecommendedBooks(pageable);
        return PageResponse.from(bestRecommendedBooks.map(MainBookResponse::from));
    }

    public BookResponse createBook(BookCreateRequest request, MultipartFile coverImage) {

        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }

        if (request.categoryIds() == null || request.categoryIds().isEmpty()) {
            throw new InvalidCategorySelectionException("최소 한 개 이상의 카테고리를 선택해야 합니다.");
        }

        if (request.categoryIds().size() > 10) {
            throw new InvalidCategorySelectionException("카테고리는 최대 10개까지만 선택할 수 있습니다.");
        }

        Publisher publisher = publisherRepository.findById(request.publisherId()).orElseThrow(() -> new PublisherNotFoundException(request.publisherId()));
        String imageUrl = minioUploader.upload(coverImage, bucket);
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
                .thumbnailUrl(imageUrl)
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

        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }

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

        book.getBookCategories().clear();
        book.getBookAuthors().clear();
        book.getBookTags().clear();

        bookRepository.delete(book);
    }

    // 알라딘 api + 자체적으로 조정할 내용 입력하여 도서 등록
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

    private double getAverageRating(Long bookId) {
        return reviewRepository.findAverageRatingByBookId(bookId)
                .orElse(0.0);
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
}