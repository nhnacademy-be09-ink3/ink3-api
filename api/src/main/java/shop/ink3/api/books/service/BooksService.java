package shop.ink3.api.books.service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;
import shop.ink3.api.books.books.entity.Books;
import shop.ink3.api.books.repository.BooksRepository;

@Service
@Transactional
public class BooksService {
    BooksRepository booksRepository;
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public void save(Books book) {
        booksRepository.save(book);
    }

    public Books findById(Long id) {
        return booksRepository.findById(id).orElse(null);
    }

    public List<Books> findAllByTitle(String title) {
        return booksRepository.getBooksByTitle(title);
    }

    public List<Books> findAllByAuthor(String author) {
        return booksRepository.findDistinctByBookAuthorsAuthorsNameContainingIgnoreCase(author);
    }


}
