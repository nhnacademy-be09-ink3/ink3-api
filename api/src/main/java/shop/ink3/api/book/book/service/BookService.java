package shop.ink3.api.book.book.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
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
import shop.ink3.api.book.book.enums.SortType;
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
import shop.ink3.api.common.util.PresignUrlPrefixUtil;
import shop.ink3.api.review.review.repository.ReviewRepository;
import shop.ink3.api.user.like.repository.LikeRepository;

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
    private final LikeRepository likeRepository;
    private final MinioUploader minioUploader;
    private final PresignUrlPrefixUtil presignUrlPrefixUtil;

    @Value("${minio.book-bucket}")
    private String bucket;

    // 단건 조회
    @Transactional(readOnly = true)
    public BookResponse getBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        double averageRating = getAverageRating(bookId);
        long likeCount = likeRepository.countByBookId(book.getId());

        String imageUrl = book.getThumbnailUrl();
        if (imageUrl != null && !imageUrl.startsWith("https")) {
            imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
        }
        return BookResponse.from(book, imageUrl, averageRating, likeCount);
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
        String imageUrl = book.getThumbnailUrl();
        if (imageUrl != null && !imageUrl.startsWith("https")) {
            imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
        }
        return BookResponse.from(book, imageUrl, categories.stream().map(CategoryResponse::from).toList());
    }

    // 전체 조회

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        Page<BookResponse> bookResponses = books.map(book -> {
            String imageUrl = book.getThumbnailUrl();
            if (imageUrl != null && !imageUrl.startsWith("https")) {
                imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
            }
            return BookResponse.from(book, imageUrl);
        });
        return PageResponse.from(bookResponses);
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getTop5BestSellerBooks() {
        Page<Book> top5BestSellerBooks = bookRepository.findBestSellerBooks(Pageable.ofSize(5));
        Page<MainBookResponse> responses = top5BestSellerBooks.map(book -> {
            String imageUrl = book.getThumbnailUrl();
            if (imageUrl != null && !imageUrl.startsWith("https")) {
                imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
            }
            return MainBookResponse.from(book, imageUrl);
        });

        return PageResponse.from(responses);
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getAllBestSellerBooks(SortType sortType, Pageable pageable) {
        Page<Book> bestSellerBooks = bookRepository.findSortedBestSellerBooks(sortType, pageable);
        Page<MainBookResponse> responses = bestSellerBooks.map(book -> {
            long reviewCount = reviewRepository.countByOrderBookBookId(book.getId());
            long likeCount = likeRepository.countByBookId(book.getId());
            String imageUrl = book.getThumbnailUrl();
            if (imageUrl != null && !imageUrl.startsWith("https")) {
                imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
            }
            return MainBookResponse.from(book, reviewCount, likeCount, imageUrl);
        });
        return PageResponse.from(responses);
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getTop5NewBooks() {
        Page<Book> top5NewBooks = bookRepository.findAllByOrderByPublishedAtDesc(Pageable.ofSize(5));
        Page<MainBookResponse> responses = top5NewBooks.map(book -> {
            String imageUrl = book.getThumbnailUrl();
            if (imageUrl != null && !imageUrl.startsWith("https")) {
                imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
            }
            return MainBookResponse.from(book, imageUrl);
        });
        return PageResponse.from(responses);
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getAllNewBooks(SortType sortType, Pageable pageable) {
        Page<Book> newBooks = bookRepository.findSortedNewBooks(sortType, pageable);
        Page<MainBookResponse> responses = newBooks.map(book -> {
            long reviewCount = reviewRepository.countByOrderBookBookId(book.getId());
            long likeCount = likeRepository.countByBookId(book.getId());
            String imageUrl = book.getThumbnailUrl();
            if (imageUrl != null && !imageUrl.startsWith("https")) {
                imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
            }
            return MainBookResponse.from(book, reviewCount, likeCount, imageUrl);
        });
        return PageResponse.from(responses);
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getTop5RecommendedBooks() {
        Page<Book> top5RecommendedBooks = bookRepository.findRecommendedBooks(Pageable.ofSize(5));
        Page<MainBookResponse> responses = top5RecommendedBooks.map(book -> {
            String imageUrl = book.getThumbnailUrl();
            if (imageUrl != null && !imageUrl.startsWith("https")) {
                imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
            }
            return MainBookResponse.from(book, imageUrl);
        });
        return PageResponse.from(responses);
    }

    @Transactional(readOnly = true)
    public PageResponse<MainBookResponse> getAllRecommendedBooks(SortType sortType, Pageable pageable) {
        Page<Book> bestRecommendedBooks = bookRepository.findSortedRecommendedBooks(sortType, pageable);
        Page<MainBookResponse> responses = bestRecommendedBooks.map(book -> {
            long reviewCount = reviewRepository.countByOrderBookBookId(book.getId());
            long likeCount = likeRepository.countByBookId(book.getId());
            String imageUrl = book.getThumbnailUrl();
            if (imageUrl != null && !imageUrl.startsWith("https")) {
                imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
            }
            return MainBookResponse.from(book, reviewCount, likeCount, imageUrl);
        });
        return PageResponse.from(responses);
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

        return BookResponse.from(bookRepository.save(book), imageUrl);
    }

    public BookResponse updateBook(Long bookId, BookUpdateRequest request, MultipartFile coverImage) {

        if (request.categoryIds() == null || request.categoryIds().isEmpty()) {
            throw new InvalidCategorySelectionException("최소 한 개 이상의 카테고리를 선택해야 합니다.");
        }

        if (request.categoryIds().size() > 10) {
            throw new InvalidCategorySelectionException("카테고리는 최대 10개까지만 선택할 수 있습니다.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        // 수정 시 중복 ISBN 허용하지 않음, request로 동일한 ISBN이 입력되어도 같은 도서이므로 throw하지 않음
        if (bookRepository.existsByIsbn(request.isbn()) && !book.getIsbn().equals(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }

        Publisher publisher = publisherRepository.findById(request.publisherId())
                .orElseThrow(() -> new PublisherNotFoundException(request.publisherId()));

        // 입력받은 이미지 파일이 없으면 기존 imageUrl로 유지
        String imageUrl = book.getThumbnailUrl();
        if (coverImage != null && !coverImage.isEmpty()) {
            minioUploader.delete(imageUrl, bucket);
            imageUrl = minioUploader.upload(coverImage, bucket);
        }

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
                imageUrl,
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
        double averageRating = getAverageRating(bookId);
        long likeCount = likeRepository.countByBookId(book.getId());

        return BookResponse.from(book, imageUrl, averageRating, likeCount);
    }

    public void deleteBook(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException(bookId));

        // 도서 삭제 로직 수정 필요 -> 실제 삭제를 하는 것이 아닌 도서의 상태를 "삭제"로 변경

        book.getBookCategories().clear();
        book.getBookAuthors().clear();
        book.getBookTags().clear();

        bookRepository.delete(book);
    }

    // 알라딘 api + 자체적으로 조정할 내용 입력하여 도서 등록
    public BookResponse registerBookByAladin(BookRegisterRequest request) {
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

        return BookResponse.from(book, book.getThumbnailUrl());
    }

    private double getAverageRating(Long bookId) {
        return reviewRepository.findAverageRatingByBookId(bookId)
                .orElse(0.0);
    }

    // 카테고리는 최소 2단계

    private void validateCategoryDepth(List<Category> categories) {
        for (Category category : categories) {
            if (category.getParent().getName().equals("ROOT")) {
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

            String name;
            String role;

            if (matcher.matches()) {
                name = matcher.group(1).trim();
                currentRole = matcher.group(2).trim();
                role = currentRole;
            } else {
                name = part;
                role = (currentRole != null) ? currentRole : "지은이";
            }
            Author author = authorRepository.findByName(name)
                    .orElseGet(() -> {
                        Author newAuthor = Author.builder()
                                .name(name)
                                .build();
                        return authorRepository.save(newAuthor);
                    });

            result.set(i, new AuthorDto(author.getId(), name, role));
        }
        return result;
    }

    // 알라딘 api에서 가져온 카테고리 이름 분리 > 계층 구조 생성 (국내도서>소설/시/희곡/한국소설)

    public Category createCategoryHierarchy(String categoryPath) {
        String[] categoryNames = categoryPath.split(">");
        Category parent = categoryRepository.findByName("ROOT")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("ROOT")
                        .parent(null)
                        .build()));

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
  
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooksByCategory(String categoryName, Pageable pageable) {
        // 1. 카테고리 엔티티 조회
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException(categoryName));

        // 2. 자기 자신 포함 하위 카테고리 ID 전체 조회
        List<Category> allCategories = categoryRepository.findAllDescendantsIncludingSelf(category.getId());
        List<Long> categoryIds = allCategories.stream()
                .map(Category::getId)
                .toList();

        // 3. 책 목록 조회
        Page<Book> books = bookRepository.findByCategoryIds(categoryIds, pageable);

        // 4. 이미지 URL 가공 및 DTO 변환
        Page<BookResponse> bookResponses = books.map(book -> {
            String imageUrl = book.getThumbnailUrl();
            if (imageUrl != null && !imageUrl.startsWith("https")) {
                imageUrl = presignUrlPrefixUtil.addPrefixUrl(minioUploader.getPresignedUrl(book.getThumbnailUrl(), bucket));
            }
            return BookResponse.from(book, imageUrl);
        });

        return PageResponse.from(bookResponses);
    }
}
