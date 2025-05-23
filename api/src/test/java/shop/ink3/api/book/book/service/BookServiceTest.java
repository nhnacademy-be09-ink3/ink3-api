package shop.ink3.api.book.book.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.author.repository.AuthorRepository;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.exception.BookNotFoundException;
import shop.ink3.api.book.book.external.aladin.AladinClient;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.book.publisher.repository.PublisherRepository;
import shop.ink3.api.book.tag.entity.Tag;
import shop.ink3.api.book.tag.repository.TagRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

class BookServiceTest {

    private Publisher publisher;
    private Author author;
    private Category category;
    private Tag tag;

    @Mock BookRepository bookRepository;
    @Mock AuthorRepository authorRepository;
    @Mock PublisherRepository publisherRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock TagRepository tagRepository;
    @Mock AladinClient aladinClient;

    @InjectMocks BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        publisher = Publisher.builder().id(1L).name("출판사").build();
        author = Author.builder().id(1L).name("저자").build();
        category = Category.builder().id(1L).name("카테고리").build();
        tag = Tag.builder().id(1L).name("태그").build();
    }

//    @Test
//    void extractKeywords_shouldHandleSpecialCharacters() throws Exception {
//        List<String> result = invokeExtractKeywords("Spring", "Boot", "Java!", "Spring?");
//        assertThat(result).containsExactly("spring","boot", "java!", "spring?");
//    }
//
//    @Test
//    void extractKeywords_shouldIgnoreDuplicatesCaseInsensitive() throws Exception {
//        List<String> result = invokeExtractKeywords("Java", "JAVA", "java", "JaVa");
//        assertThat(result).containsExactly("java");
//    }
//
//    @SuppressWarnings("unchecked")
//    private List<String> invokeExtractKeywords(String... texts) throws Exception {
//        var clazz = BookService.class;
//        var m = clazz.getDeclaredMethod("extractKeywords", String[].class);
//        m.setAccessible(true);
//        return (List<String>) m.invoke(new BookService(null, null, null, null, null, null), (Object) texts);
//    }

//    @Test
//    void createBook_shouldCreateSuccessfully() {
//        BookCreateRequest request = new BookCreateRequest(
//                "ISBN123", "Title", "TOC", "Description", LocalDate.now(),
//                20000, 18000, 10, BookStatus.AVAILABLE, true, "url",
//                1L, List.of(1L, 2L), List.of(1L), List.of(1L)
//        );
//        Category category1 = Category.builder().id(1L).name("카테고리1").build();
//        Category category2 = Category.builder().id(2L).name("카테고리2").build();
//
//        given(publisherRepository.findById(1L)).willReturn(Optional.of(publisher));
//        given(categoryRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(category1, category2));
//        given(authorRepository.findAllById(any())).willReturn(List.of(author));
//        given(tagRepository.findAllById(any())).willReturn(List.of(tag));
//        given(bookRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
//
//        BookResponse response = bookService.createBook(request);
//        assertThat(response.title()).isEqualTo("Title");
//    }

//    @Test
//    void registerBookByIsbn_shouldSucceed() {
//        String isbn = "9876543210000";
//        AladinBookResponse dto = new AladinBookResponse(
//                "알라딘책", "desc", "toc", "작가", "출판사", "2024-01-01", isbn,
//                30000, "cover", "IT > 웹 > 자바"
//        );
//
//        given(bookRepository.existsByIsbn(isbn)).willReturn(false);
//        given(aladinClient.fetchBookByIsbn(isbn)).willReturn(dto);
//        given(publisherRepository.findByName("출판사")).willReturn(Optional.of(Publisher.builder().name("출판사").build()));
//        given(authorRepository.findByName("작가")).willReturn(Optional.of(Author.builder().name("작가").build()));
//        given(categoryRepository.findByName("자바")).willReturn(Optional.of(Category.builder().name("자바").build()));
//        given(tagRepository.findByName(any())).willReturn(Optional.empty());
//        given(tagRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
//        given(bookRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
//
//        BookResponse response = bookService.registerBookByIsbn(isbn);
//        assertThat(response.title()).isEqualTo("알라딘책");
//    }
//
//    @Test
//    void registerBookByIsbn_whenDuplicate_shouldThrowException() {
//        given(bookRepository.existsByIsbn("123")).willReturn(true);
//        assertThatThrownBy(() -> bookService.registerBookByIsbn("123"))
//                .isInstanceOf(DuplicateIsbnException.class);
//    }

    @Test
    void findById_shouldReturnBook() {
        Book book = Book.builder()
                .id(1L)
                .title("단건조회")
                .originalPrice(10000)
                .salePrice(9000)
                .publishedAt(LocalDate.now())
                .status(BookStatus.AVAILABLE)
                .publisher(Publisher.builder().name("출판사").build())
                .build();

        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        BookResponse response = bookService.getBook(1L);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("단건조회");
        assertThat(response.originalPrice()).isEqualTo(10000);
        assertThat(response.salePrice()).isEqualTo(9000);
        assertThat(response.publishedAt()).isNotNull();
        assertThat(response.publisherName()).isEqualTo("출판사");
    }

    @Test
    void findById_whenNotFound_shouldThrowException() {
        given(bookRepository.findById(2L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.getBook(2L))
                .isInstanceOf(BookNotFoundException.class);
    }

//    @Test
//    void searchBooks_byTitle_shouldReturnPage() {
//        Book book = Book.builder()
//                .id(1L).title("검색책").originalPrice(10000).salePrice(9000)
//                .publishedAt(LocalDate.now()).status(BookStatus.AVAILABLE)
//                .publisher(Publisher.builder().name("출판사").build())
//                .build();
//
//        List<Book> books = List.of(book);
//        Page<Book> page = new PageImpl<>(books, PageRequest.of(0, 10), 1);
//
//        given(bookRepository.findByTitleContainingIgnoreCase(eq("검색"), any())).willReturn(page);
//
//        BookSearchRequest request = new BookSearchRequest("검색", null, null, null, null, null, BookSortType.TITLE_ASC, 0,
//                10);
//        var result = bookService.searchBooks(request);
//
//        assertThat(result.content()).hasSize(1);
//        assertThat(result.content().get(0).title()).isEqualTo("검색책");
//        assertThat(result.content().get(0).originalPrice()).isEqualTo(10000);
//        assertThat(result.content().get(0).salePrice()).isEqualTo(9000);
//    }

//    @Test
//    void searchBooks_byAuthor_shouldReturnPage() {
//        Book book = Book.builder()
//                .id(1L).title("저자책").originalPrice(10000).salePrice(9000)
//                .publishedAt(LocalDate.now()).status(BookStatus.AVAILABLE)
//                .publisher(Publisher.builder().name("출판사").build())
//                .build();
//
//        List<Book> books = List.of(book);
//        Page<Book> page = new PageImpl<>(books, PageRequest.of(0, 10), 1);
//
//        given(bookRepository.findDistinctByBookAuthorsAuthorNameContainingIgnoreCase(eq("홍길동"), any())).willReturn(
//                page);
//
//        BookSearchRequest request = new BookSearchRequest(null, "홍길동", null, null, null, null, BookSortType.TITLE_ASC,
//                0, 10);
//        var result = bookService.searchBooks(request);
//
//        assertThat(result.content()).hasSize(1);
//        assertThat(result.content().get(0).title()).isEqualTo("저자책");
//        assertThat(result.content().get(0).originalPrice()).isEqualTo(10000);
//        assertThat(result.content().get(0).salePrice()).isEqualTo(9000);
//    }

    @Test
    void deleteBook_shouldSucceed() {
        Book book = Book.builder().id(1L).title("삭제될책").build();
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        bookService.deleteBook(1L);
        then(bookRepository).should().delete(book);
    }

    @Test
    void deleteBook_whenNotFound_shouldThrowException() {
        given(bookRepository.findById(999L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(BookNotFoundException.class);
    }


//    @Test
//    void createBook_whenTooManyCategories_shouldThrowException() {
//        BookCreateRequest request = new BookCreateRequest(
//                "ISBN123", "TooManyCats", "TOC", "Desc", LocalDate.now(),
//                10000, 9000, 5, BookStatus.AVAILABLE, true, "url",
//                1L, List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L), List.of(1L), List.of(1L)
//        );
//        assertThatThrownBy(() -> bookService.createBook(request))
//                .isInstanceOf(shop.ink3.api.book.book.exception.InvalidCategorySelectionException.class);
//    }

//    @Test
//    void updateBook_whenTooManyCategories_shouldThrowException() {
//        Book book = Book.builder().id(1L).title("old").build();
//        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
//        given(publisherRepository.findById(anyLong())).willReturn(
//                Optional.of(Publisher.builder().id(1L).name("출판사").build()));
//
//        BookUpdateRequest request = new BookUpdateRequest(
//                "ISBN", "title", "contents", "desc", LocalDate.now(), 10000, 9000, 5,
//                BookStatus.AVAILABLE, true, "url", 1L,
//                List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L), List.of(1L), List.of(1L)
//        );
//
//        assertThatThrownBy(() -> bookService.updateBook(1L, request))
//                .isInstanceOf(shop.ink3.api.book.book.exception.InvalidCategorySelectionException.class);
//    }

//    @Test
//    void registerBookByIsbn_withCategoryWithoutDelimiter_shouldSucceed() {
//        String isbn = "isbn-delimiter";
//        AladinBookResponse dto = new AladinBookResponse(
//                "title", "desc", "toc", "작가", "출판사", "2024-01-01", isbn,
//                30000, "cover", "카테고리단일"
//        );
//
//        given(bookRepository.existsByIsbn(isbn)).willReturn(false);
//        given(aladinClient.fetchBookByIsbn(isbn)).willReturn(dto);
//        given(publisherRepository.findByName(any())).willReturn(Optional.of(Publisher.builder().name("출판사").build()));
//        given(authorRepository.findByName(any())).willReturn(Optional.of(Author.builder().name("작가").build()));
//        given(categoryRepository.findByName(any())).willReturn(Optional.of(Category.builder().name("카테고리단일").build()));
//        given(tagRepository.findByName(any())).willReturn(Optional.empty());
//        given(tagRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
//        given(bookRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
//
//        BookResponse response = bookService.registerBookByIsbn(isbn);
//        assertThat(response.title()).isEqualTo("title");
//    }


//    @Test
//    void updateBook_whenPublisherNotFound_shouldThrowException() {
//        Book book = createBasicBook();
//        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
//        given(publisherRepository.findById(anyLong())).willReturn(Optional.empty());
//
//        BookUpdateRequest request = new BookUpdateRequest(
//                "isbn", "title", "toc", "desc", LocalDate.now(),
//                10000, 9000, 10, BookStatus.AVAILABLE, true, "url",
//                999L, List.of(1L), List.of(1L), List.of(1L)
//        );
//
//        assertThatThrownBy(() -> bookService.updateBook(1L, request))
//                .isInstanceOf(shop.ink3.api.book.publisher.exception.PublisherNotFoundException.class);
//    }

//    @Test
//    void updateBook_shouldUpdateAllFieldsSuccessfully() {
//        Book book = createBasicBook();
//        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
//        mockAllLookups();
//
//        BookUpdateRequest request = new BookUpdateRequest(
//                "isbn", "new title", "toc", "desc", LocalDate.now(),
//                20000, 15000, 5, BookStatus.AVAILABLE, false, "thumb",
//                1L, List.of(1L), List.of(1L), List.of(1L)
//        );
//
//        BookResponse response = bookService.updateBook(1L, request);
//        assertThat(response.title()).isEqualTo("new title");
//    }

//    @Test
//    void searchBooks_whenNoTitleAndAuthor_shouldReturnAll() {
//        Book book = Book.builder().id(1L).title("모두조회").originalPrice(10000).salePrice(9000)
//                .publishedAt(LocalDate.now()).status(BookStatus.AVAILABLE)
//                .publisher(Publisher.builder().name("출판사").build())
//                .build();
//        Page<Book> page = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);
//
//        given(bookRepository.findAll(any(PageRequest.class))).willReturn(page);
//
//        BookSearchRequest request = new BookSearchRequest(null, null, null, null, null, null, BookSortType.TITLE_ASC, 0,
//                10);
//        var result = bookService.searchBooks(request);
//        assertThat(result.content()).hasSize(1);
//    }

//    @Test
//    void updateBook_whenAuthorNotFound_shouldThrowException() {
//        Book book = createBasicBook();
//        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
//        given(publisherRepository.findById(1L)).willReturn(Optional.of(publisher));
//        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
//        given(categoryRepository.findById(2L)).willReturn(Optional.of(Category.builder().id(2L).name("서브카테고리").build()));
//        given(categoryRepository.findAllById(any()))
//                .willReturn(List.of(
//                        Category.builder().id(1L).name("카테고리1").build(),
//                        Category.builder().id(2L).name("카테고리2").build()
//                ));
//
//        given(authorRepository.findById(1L)).willReturn(Optional.empty());
//        given(tagRepository.findById(1L)).willReturn(Optional.of(tag));
//
//        BookUpdateRequest request = new BookUpdateRequest(
//                "isbn", "title", "toc", "desc", LocalDate.now(), 10000, 9000, 10,
//                BookStatus.AVAILABLE, true, "url", 1L,
//                List.of(1L, 2L),
//                List.of(1L),
//                List.of(1L)
//        );
//
//        assertThatThrownBy(() -> bookService.updateBook(1L, request))
//                .isInstanceOf(shop.ink3.api.book.author.exception.AuthorNotFoundException.class);
//    }

//    @Test
//    void updateBook_whenCategoryNotFound_shouldThrowException() {
//        Book book = createBasicBook();
//        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
//        mockAllLookups();
//        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());
//
//        BookUpdateRequest request = new BookUpdateRequest(
//                "isbn", "title", "toc", "desc", LocalDate.now(), 10000, 9000, 10,
//                BookStatus.AVAILABLE, true, "url", 1L,
//                List.of(1L, 2L),
//                List.of(1L), List.of(1L)
//        );
//
//        assertThatThrownBy(() -> bookService.updateBook(1L, request))
//                .isInstanceOf(shop.ink3.api.book.category.exception.CategoryNotFoundException.class);
//    }

//    @Test
//    void updateBook_whenTagNotFound_shouldThrowException() {
//        Book book = createBasicBook();
//        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
//        mockAllLookups();
//        given(tagRepository.findById(anyLong())).willReturn(Optional.empty());
//
//        BookUpdateRequest request = new BookUpdateRequest(
//                "isbn", "title", "toc", "desc", LocalDate.now(), 10000, 9000, 10,
//                BookStatus.AVAILABLE, true, "url", 1L,
//                List.of(2L),
//                List.of(1L), List.of(1L)
//        );
//
//        assertThatThrownBy(() -> bookService.updateBook(1L, request))
//                .isInstanceOf(shop.ink3.api.book.tag.exception.TagNotFoundException.class);
//    }

//    @Test
//    void registerBookByIsbn_whenCategoryNameNull_shouldSucceedWithoutCategory() {
//        String isbn = "no-category";
//        AladinBookResponse dto = new AladinBookResponse(
//                "title", "desc", "toc", "작가", "출판사", "2024-01-01", isbn,
//                30000, "cover", null
//        );
//
//        given(bookRepository.existsByIsbn(isbn)).willReturn(false);
//        given(aladinClient.fetchBookByIsbn(isbn)).willReturn(dto);
//        given(publisherRepository.findByName(any())).willReturn(Optional.of(Publisher.builder().name("출판사").build()));
//        given(authorRepository.findByName(any())).willReturn(Optional.of(Author.builder().name("작가").build()));
//        given(tagRepository.findByName(any())).willReturn(Optional.empty());
//        given(tagRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
//        given(bookRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
//
//        BookResponse response = bookService.registerBookByIsbn(isbn);
//        assertThat(response.title()).isEqualTo("title");
//    }

    private Book createBasicBook() {
        return Book.builder()
                .id(1L)
                .title("기본책")
                .originalPrice(10000)
                .salePrice(9000)
                .publishedAt(LocalDate.now())
                .status(BookStatus.AVAILABLE)
                .publisher(publisher)
                .build();
    }

    private void mockAllLookups() {
        given(publisherRepository.findById(anyLong())).willReturn(Optional.of(publisher));
        given(authorRepository.findById(anyLong())).willReturn(Optional.of(author));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(tagRepository.findById(anyLong())).willReturn(Optional.of(tag));
        given(categoryRepository.findAllById(any()))
                .willReturn(List.of(
                        Category.builder().id(1L).name("IT 모바일").build(),
                        Category.builder().id(2L).name("웹사이트").build()
                ));
    }

//    @Test
//    void findAllByTitle_shouldReturnBooks() {
//        Book book = createBasicBook();
//        given(bookRepository.getBooksByTitle("제목")).willReturn(List.of(book));
//
//        List<BookResponse> result = bookService.findAllByTitle("제목");
//
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).title()).isEqualTo("기본책");
//        assertThat(result.get(0).originalPrice()).isEqualTo(10000);
//        assertThat(result.get(0).salePrice()).isEqualTo(9000);
//    }
//
//    @Test
//    void findAllByAuthor_shouldReturnBooks() {
//        Book book = createBasicBook();
//        given(bookRepository.findDistinctByBookAuthorsAuthorNameContainingIgnoreCase("작가")).willReturn(List.of(book));
//
//        List<BookResponse> result = bookService.findAllByAuthor("작가");
//
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).title()).isEqualTo("기본책");
//        assertThat(result.get(0).originalPrice()).isEqualTo(10000);
//        assertThat(result.get(0).salePrice()).isEqualTo(9000);
//    }

//    @Test
//    void extractKeywords_shouldHandleNullAndEmptyStrings() throws Exception {
//        List<String> result = invokeExtractKeywords(" ", "", null);
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void extractKeywords_shouldLimitToTop5() throws Exception {
//        List<String> result = invokeExtractKeywords("one", "two", "three", "four", "five", "six", "seven");
//        assertThat(result).hasSize(5);
//    }

//    @Test
//    void createBook_shouldFailWhenCategoryDepthIsLessThanTwo() {
//
//        Category flatCategory = Category.builder().id(99L).name("단일카테고리").build();
//        given(categoryRepository.findAllById(any())).willReturn(List.of(flatCategory));
//        given(publisherRepository.findById(anyLong())).willReturn(Optional.of(publisher));
//        given(authorRepository.findAllById(any())).willReturn(List.of(author));
//        given(tagRepository.findAllById(any())).willReturn(List.of(tag));
//
//        BookCreateRequest request = new BookCreateRequest(
//                "ISBN999", "Flat", "목차", "설명", LocalDate.now(),
//                10000, 9000, 10, BookStatus.AVAILABLE, false, "thumb",
//                1L, List.of(99L), List.of(1L), List.of(1L)
//        );
//
//        assertThatThrownBy(() -> bookService.createBook(request))
//                .isInstanceOf(shop.ink3.api.book.book.exception.InvalidCategoryDepthException.class);
//    }

    @Test
    void calculateDiscountRate_shouldReturnCorrectPercentage() {
        Book book = Book.builder()
                .id(1L)
                .title("할인책")
                .originalPrice(20000)
                .salePrice(15000)
                .publishedAt(LocalDate.now())
                .status(BookStatus.AVAILABLE)
                .publisher(publisher)
                .build();

        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        BookResponse result = bookService.getBook(1L);

        assertThat(result.originalPrice()).isEqualTo(20000);
        assertThat(result.salePrice()).isEqualTo(15000);
        assertThat(result.discountRate()).isEqualTo(25); // 25% 할인
    }

}