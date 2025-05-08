package shop.ink3.api.books.service;

import org.springframework.stereotype.Service;
import shop.ink3.api.books.repository.BooksRepository;

@Service
public class BooksService {
    BooksRepository booksRepository;
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }
}
