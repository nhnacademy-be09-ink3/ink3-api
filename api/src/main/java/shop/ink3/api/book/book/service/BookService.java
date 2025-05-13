package shop.ink3.api.book.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.author.exception.AuthorNotFoundException;
import shop.ink3.api.book.author.repository.AuthorRepository;
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.entity.Book;
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
import shop.ink3.api.book.tag.repository.TagRepository;

@Service
@Transactional
public class BookService {
    final
    BookRepository bookRepository;
    final
    CategoryRepository categoryRepository;
    final
    AuthorRepository authorRepository;
    final
    PublisherRepository publisherRepository;
    final
    TagRepository tagRepository;

    public BookService(AuthorRepository authorRepository, BookRepository bookRepository, CategoryRepository categoryRepository, PublisherRepository publisherRepository,
                       TagRepository tagRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
        this.tagRepository = tagRepository;
    }

    public Book save(BookCreateRequest req) {
        Publisher publisher = publisherRepository.findById(req.publisherId()).orElseThrow(()->new PublisherNotFoundException(req.publisherId()));
        Book book = Book.builder()
            .ISBN(req.ISBN())
            .title(req.title())
            .contents(req.contents())
            .description(req.description())
            .publishedAt(req.publishedAt())
            .originalPrice(req.originalPrice())
            .salePrice(req.salePrice())
            .quantity(req.quantity())
            .status(req.status())
            .isPackable(req.isPackable())
            .thumbnailUrl(req.thumbnailUrl())
            .publisher(publisher)
        .build();

        List<Category> categoryList = categoryRepository.findAllById(req.categoryIdList());
        for(Category category : categoryList) {
            book.addBookCategory(category);
        }

        List<Author> authorList = authorRepository.findAllById(req.authorIdList());
        for(Author author : authorList) {
            book.addBookAuthor(author);
        }

        List<Tag> tagList = tagRepository.findAllById(req.tagIdList());
        for(Tag tag : tagList) {
            book.addBookTag(tag);
        }

        bookRepository.save(book);
        return book;
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> findAllByTitle(String title) {
        return bookRepository.getBooksByTitle(title);
    }

    public List<Book> findAllByAuthor(String author) {
        return bookRepository.findDistinctByBookAuthorsAuthorNameContainingIgnoreCase(author);
    }


}
