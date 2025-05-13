package shop.ink3.api.book.book.service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.repository.BookRepository;

@Service
@Transactional
public class BookService {
    BookRepository bookRepository;
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void save(Book book) {
        bookRepository.save(book);
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
