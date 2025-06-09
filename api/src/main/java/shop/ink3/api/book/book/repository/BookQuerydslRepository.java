package shop.ink3.api.book.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.enums.SortType;

public interface BookQuerydslRepository {
    Page<Book> findSortedBestSellerBooks(SortType sortType, Pageable pageable);

    Page<Book> findSortedNewBooks(SortType sortType, Pageable pageable);

    Page<Book> findSortedRecommendedBooks(SortType sortType, Pageable pageable);
}
