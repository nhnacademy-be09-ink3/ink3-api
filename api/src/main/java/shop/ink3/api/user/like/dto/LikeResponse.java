package shop.ink3.api.user.like.dto;

import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.user.like.entity.Like;

public record LikeResponse(
        Long id,
        Long userId,
        Long bookId,
        String bookTitle,
        String bookThumbnailUrl,
        Integer originalPrice,
        Integer salePrice,
        Integer discountRate,
        BookStatus bookStatus
) {
    public static LikeResponse from(Like like) {
        return new LikeResponse(
                like.getId(),
                like.getUser().getId(),
                like.getBook().getId(),
                like.getBook().getTitle(),
                like.getBook().getThumbnailUrl(),
                like.getBook().getOriginalPrice(),
                like.getBook().getSalePrice(),
                like.getBook().getDiscountRate(),
                like.getBook().getStatus()
        );
    }

    public static LikeResponse from(long userId, Like like) {
        return new LikeResponse(
                like.getId(),
                userId,
                like.getBook().getId(),
                like.getBook().getTitle(),
                like.getBook().getThumbnailUrl(),
                like.getBook().getOriginalPrice(),
                like.getBook().getSalePrice(),
                like.getBook().getDiscountRate(),
                like.getBook().getStatus()
        );
    }
}
