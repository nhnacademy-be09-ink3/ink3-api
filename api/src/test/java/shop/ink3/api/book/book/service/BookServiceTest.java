package shop.ink3.api.book.book.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.author.repository.AuthorRepository;
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.BookSearchRequest;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.exception.DuplicateIsbnException;
import shop.ink3.api.book.book.external.aladin.AladinClient;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookDto;
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

    @Mock
    BookRepository bookRepository;
    @Mock
    AuthorRepository authorRepository;
    @Mock
    PublisherRepository publisherRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    TagRepository tagRepository;
    @Mock
    AladinClient aladinClient;

    @InjectMocks
    BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldSaveBookSuccessfully() {
        BookCreateRequest request = new BookCreateRequest(
                "1234567890123", "테스트책", "목차", "설명", LocalDate.now(),
                20000, 15000, 10, BookStatus.AVAILABLE, true, "url",
                1L, List.of(1L), List.of(1L), List.of(1L)
        );

        Publisher publisher = Publisher.builder().id(1L).name("출판사").build();
        Author author = Author.builder().id(1L).name("저자").build();
        Category category = Category.builder().id(1L).name("카테고리").build();
        Tag tag = Tag.builder().id(1L).name("태그").build();

        given(publisherRepository.findById(1L)).willReturn(Optional.of(publisher));
        given(authorRepository.findAllById(any())).willReturn(List.of(author));
        given(categoryRepository.findAllById(any())).willReturn(List.of(category));
        given(tagRepository.findAllById(any())).willReturn(List.of(tag));
        given(bookRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        BookResponse result = bookService.save(request);

        assertThat(result.title()).isEqualTo("테스트책");
        assertThat(result.authors()).contains("저자");
        assertThat(result.categories()).contains("카테고리");
        assertThat(result.tags()).contains("태그");
    }

    @Test
    void registerBookByIsbn_shouldSaveBookFromAladin() {
        String isbn = "9876543210000";
        AladinBookDto dto = new AladinBookDto(
                "알라딘책", "desc", "toc", "작가", "출판사", "2024-01-01", isbn,
                30000, 25000, "cover", "IT > 웹 > 자바"
        );

        given(bookRepository.existsByISBN(isbn)).willReturn(false);
        given(aladinClient.fetchBookByIsbn(isbn)).willReturn(dto);
        given(publisherRepository.findByName("출판사")).willReturn(Optional.of(Publisher.builder().name("출판사").build()));
        given(authorRepository.findByName("작가")).willReturn(Optional.of(Author.builder().name("작가").build()));
        given(categoryRepository.findByName("자바")).willReturn(Optional.of(Category.builder().name("자바").build()));
        given(tagRepository.findByName(any())).willReturn(Optional.empty());
        given(tagRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        given(bookRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        BookResponse result = bookService.registerBookByIsbn(isbn);

        assertThat(result.title()).isEqualTo("알라딘책");
        assertThat(result.publisherName()).isEqualTo("출판사");
    }

    @Test
    void registerBookByIsbn_whenDuplicate_shouldThrowException() {
        given(bookRepository.existsByISBN("123")).willReturn(true);

        assertThatThrownBy(() -> bookService.registerBookByIsbn("123"))
                .isInstanceOf(DuplicateIsbnException.class);
    }

    @Test
    void searchBooks_shouldReturnList() {
        Book book = Book.builder()
                .id(1L)
                .title("검색책")
                .description("desc")
                .contents("toc")
                .ISBN("123")
                .thumbnailUrl("cover")
                .originalPrice(20000)
                .salePrice(15000)
                .isPackable(true)
                .publishedAt(LocalDate.now())
                .status(BookStatus.AVAILABLE)
                .publisher(Publisher.builder().name("출판사").build())
                .build();

        given(bookRepository.findAll()).willReturn(List.of(book));

        BookSearchRequest request = new BookSearchRequest("","",null,null,null,null,null,null,null);

        List<BookResponse> results = bookService.searchBooks(request);
        assertThat(results).hasSize(1);
    }
}