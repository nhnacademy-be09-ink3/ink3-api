package shop.ink3.api.book.book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.JPQLQuery;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.QBook;
import shop.ink3.api.book.book.enums.SortType;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.order.entity.QOrder;
import shop.ink3.api.order.orderBook.entity.QOrderBook;
import shop.ink3.api.user.like.entity.QLike;
import shop.ink3.api.review.review.entity.QReview;

public class BookQuerydslRepositoryImpl extends QuerydslRepositorySupport implements BookQuerydslRepository {

    public BookQuerydslRepositoryImpl() {
        super(Book.class);
    }

    @Override
    public Page<Book> findSortedBestSellerBooks(SortType sortType, Pageable pageable) {
        QBook book = QBook.book;
        QOrderBook ob = QOrderBook.orderBook;
        QOrder o = QOrder.order;
        QReview review = QReview.review;
        QLike like = QLike.like;

        JPQLQuery<Book> query = from(ob)
            .join(ob.book, book)
            .join(ob.order, o)
            .leftJoin(review).on(review.orderBook.book.eq(book))
            .leftJoin(like).on(like.book.eq(book))
            .where(o.status.eq(OrderStatus.DELIVERED))
            .select(book)
            .groupBy(book.id);

        applySort(query, sortType, book, review, like);
        JPQLQuery<Book> pagedQuery = getQuerydsl().applyPagination(pageable, query);

        List<Book> content = pagedQuery.fetch();
        long total = query.fetch().size();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Book> findSortedNewBooks(SortType sortType, Pageable pageable) {
        QBook book = QBook.book;
        QReview review = QReview.review;
        QLike like = QLike.like;

        JPQLQuery<Book> query = from(book)
            .leftJoin(review).on(review.orderBook.book.eq(book))
            .leftJoin(like).on(like.book.eq(book))
            .select(book)
            .groupBy(book.id)
            .orderBy(book.publishedAt.desc());

        applySort(query, sortType, book, review, like);
        JPQLQuery<Book> pagedQuery = getQuerydsl().applyPagination(pageable, query);

        List<Book> content = pagedQuery.fetch();
        long total = query.fetch().size();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Book> findSortedRecommendedBooks(SortType sortType, Pageable pageable) {
        QBook book = QBook.book;
        QReview review = QReview.review;
        QLike like = QLike.like;

        JPQLQuery<Book> query = from(book)
            .join(like).on(like.book.eq(book))
            .leftJoin(review).on(review.orderBook.book.eq(book))
            .select(book)
            .groupBy(book.id);

        applySort(query, sortType, book, review, like);
        JPQLQuery<Book> pagedQuery = getQuerydsl().applyPagination(pageable, query);

        List<Book> content = pagedQuery.fetch();
        long total = query.fetch().size();
        return new PageImpl<>(content, pageable, total);
    }

    private void applySort(JPQLQuery<Book> query, SortType sortType, QBook book, QReview review, QLike like) {
        switch (sortType) {
            case REVIEW -> query.orderBy(review.count().desc());
            case LIKE -> query.orderBy(like.count().desc());
            case TITLE -> query.orderBy(book.title.asc());
        }
    }
}
