CREATE TABLE `admins`
(
    `id`            bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `login_id`      varchar(20)  NOT NULL UNIQUE,
    `password`      varchar(100) NOT NULL,
    `name`          varchar(20)  NOT NULL,
    `status`        varchar(20)  NOT NULL,
    `last_login_at` datetime     NULL,
    `created_at`    datetime     NOT NULL
);

CREATE TABLE `memberships`
(
    `id`               bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`             varchar(20) NOT NULL,
    `condition_amount` int         NOT NULL,
    `point_rate`       int         NOT NULL,
    `is_active`        boolean     NOT NULL,
    `is_default`       boolean     NOT NULL,
    `created_at`       datetime    NOT NULL
);

CREATE TABLE `users`
(
    `id`            bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `login_id`      varchar(20)  NOT NULL UNIQUE,
    `password`      varchar(100) NOT NULL,
    `name`          varchar(20)  NOT NULL UNIQUE,
    `email`         varchar(50)  NOT NULL UNIQUE,
    `phone`         varchar(20)  NOT NULL,
    `birthday`      date         NOT NULL,
    `point`         int          NOT NULL,
    `status`        varchar(20)  NOT NULL,
    `last_login_at` datetime     NULL,
    `created_at`    datetime     NOT NULL,
    `membership_id` bigint       NOT NULL,
    FOREIGN KEY (`membership_id`) REFERENCES `memberships` (`id`)
);

CREATE TABLE `publishers`
(
    `id`   bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name` varchar(20) NOT NULL
);

CREATE TABLE `books`
(
    `id`             bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `ISBN`           varchar(20)  NOT NULL,
    `title`          varchar(255) NOT NULL,
    `contents`       text         NOT NULL,
    `description`    text         NOT NULL,
    `publisher_id`   bigint       NOT NULL,
    `published_at`   date         NOT NULL,
    `original_price` int          NOT NULL,
    `sale_price`     int          NOT NULL,
    `discount_rate`  int          NOT NULL,
    `quantity`       int          NOT NULL,
    `status`         varchar(20)  NOT NULL,
    `is_packable`    boolean      NOT NULL,
    `thumbnail_url`  varchar(255) NOT NULL,
    FOREIGN KEY (`publisher_id`) REFERENCES `publishers` (`id`)
);

CREATE TABLE `likes`
(
    `id`      bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `book_id` bigint NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
);

CREATE TABLE `categories`
(
    `id`        bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`      varchar(20) NOT NULL,
    `parent_id` bigint      NULL,
    FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`)
);

CREATE TABLE `coupon_policies`
(
    `id`                      bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`                    varchar(20) NOT NULL,
    `minimum_order_amount`    int         NOT NULL,
    `discount_type`           varchar(20) NOT NULL,
    `discount_value`          int         NULL,
    `discount_percentage`     int         NULL,
    `maximum_discount_amount` int         NULL,
    `valid_days`              datetime    NOT NULL
);

CREATE TABLE `coupons`
(
    `id`               bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `coupon_policy_id` bigint      NOT NULL,
    `name`             varchar(20) NOT NULL,
    `trigger_type`     varchar(20) NOT NULL,
    FOREIGN KEY (`coupon_policy_id`) REFERENCES `coupon_policies` (`id`)
);

CREATE TABLE `coupon_stores`
(
    `id`          bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`     bigint      NOT NULL,
    `coupon_id`   bigint      NOT NULL,
    `created_at`  DateTime    NOT NULL,
    `used_at`     datetime    NOT NULL,
    `valid_from`  datetime    NOT NULL,
    `valid_until` datetime    NOT NULL,
    `status`      varchar(20) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`)
);

CREATE TABLE `category_coupons`
(
    `id`          bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `coupon_id`   bigint NOT NULL,
    `category_id` bigint NOT NULL,
    FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`),
    FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
);

CREATE TABLE `addresses`
(
    `id`              bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`         bigint       NOT NULL,
    `name`            varchar(20)  NOT NULL,
    `postal_code`     varchar(20)  NOT NULL,
    `default_address` varchar(100) NOT NULL,
    `detail_address`  varchar(100) NOT NULL,
    `extra_address`   varchar(50)  NULL,
    `is_default`      boolean      NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `authors`
(
    `id`          bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`        varchar(20)  NOT NULL,
    `birth`       date         NOT NULL,
    `nationality` varchar(20)  NOT NULL,
    `biography`   varchar(255) NOT NULL
);

CREATE TABLE `book_authors`
(
    `id`        bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `book_id`   bigint NOT NULL,
    `author_id` bigint NOT NULL,
    FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
    FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`)
);

CREATE TABLE `packagings`
(
    `id`           bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`         varchar(20) NOT NULL,
    `price`        int         NOT NULL,
    `is_available` boolean     NOT NULL
);

CREATE TABLE `orders`
(
    `id`              bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `status`          varchar(20) NOT NULL,
    `ordered_at`      datetime    NOT NULL,
    `orderer_name`    varchar(20) NOT NULL,
    `orderer_phone`   varchar(20) NOT NULL,
    `user_id`         bigint      NULL,
    `coupon_store_id` bigint      NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`coupon_store_id`) REFERENCES `coupon_stores` (`id`)
);

CREATE TABLE `guest_order_accesses`
(
    `id`       bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `order_id` bigint       NOT NULL,
    `email`    varchar(50)  NOT NULL,
    `password` varchar(100) NOT NULL,
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
);

CREATE TABLE `book_coupons`
(
    `id`        bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `coupon_id` bigint NOT NULL,
    `book_id`   bigint NOT NULL,
    FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`),
    FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
);

CREATE TABLE `order_books`
(
    `id`              bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `order_id`        bigint NOT NULL,
    `book_id`         bigint NOT NULL,
    `packaging_id`    bigint NULL,
    `coupon_store_id` bigint NULL,
    `price`           int    NOT NULL,
    `quantity`        int    NOT NULL,
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
    FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
    FOREIGN KEY (`packaging_id`) REFERENCES `packagings` (`id`),
    FOREIGN KEY (`coupon_store_id`) REFERENCES `coupon_stores` (`id`)
);

CREATE TABLE `refunds`
(
    `id`       bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `order_id` bigint       NOT NULL,
    `reason`   varchar(20)  NOT NULL,
    `details`  varchar(255) NULL,
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
);

CREATE TABLE `reviews`
(
    `id`            bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`       bigint      NOT NULL,
    `order_book_id` bigint      NOT NULL,
    `title`         varchar(50) NOT NULL,
    `content`       text        NOT NULL,
    `rating`        int         NOT NULL,
    `created_at`    datetime    NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`order_book_id`) REFERENCES `order_books` (`id`)
);

CREATE TABLE `shipments`
(
    `id`                      bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `order_id`                bigint       NOT NULL,
    `preferred_delivery_date` date         NOT NULL,
    `delivered_at`            datetime     NOT NULL,
    `recipient_name`          varchar(50)  NOT NULL,
    `recipient_phone`         varchar(20)  NOT NULL,
    `postal_code`             int          NOT NULL,
    `default_address`         varchar(100) NOT NULL,
    `detail_address`          varchar(100) NOT NULL,
    `extra_address`           varchar(100) NOT NULL,
    `shipping_fee`            int          NOT NULL,
    `shipping_code`           varchar(20)  NULL,
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
);

CREATE TABLE `refund_policies`
(
    `id`                      bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`                    varchar(20) NOT NULL,
    `return_dead_line`        int         NOT NULL,
    `defect_return_dead_line` int         NOT NULL,
    `is_available`            boolean     NOT NULL,
    `created_at`              datetime    NOT NULL
);

CREATE TABLE `point_policies`
(
    `id`           bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`         varchar(20) NOT NULL,
    `earn_point`   int         NOT NULL,
    `created_at`   datetime    NOT NULL,
    `is_available` boolean     NOT NULL
);

CREATE TABLE `tags`
(
    `id`   bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name` varchar(20) NOT NULL
);

CREATE TABLE `book_categories`
(
    `id`          bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `book_id`     bigint NOT NULL,
    `category_id` bigint NOT NULL,
    FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
    FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
);

CREATE TABLE `shipping_policies`
(
    `id`           bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`         varchar(20) NOT NULL,
    `threshold`    int         NOT NULL,
    `fee`          int         NOT NULL,
    `is_available` boolean     NOT NULL,
    `created_at`   datetime    NOT NULL
);

CREATE TABLE `book_tags`
(
    `id`      bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `book_id` bigint NOT NULL,
    `tag_id`  bigint NOT NULL,
    FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
    FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
);

CREATE TABLE `point_histories`
(
    `id`         bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`    bigint      NOT NULL,
    `delta`      int         NULL,
    `status`     varchar(20) NULL,
    `created_at` datetime    NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `payments`
(
    `id`             bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `order_id`       bigint       NOT NULL,
    `payment_key`    varchar(100) NOT NULL,
    `payment_amount` int          NOT NULL,
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
);

CREATE TABLE `carts`
(
    `id`       bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`  bigint NOT NULL,
    `book_id`  bigint NOT NULL,
    `quantity` int    NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
);

CREATE TABLE `socials`
(
    `id`               bigint       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`          bigint       NOT NULL,
    `provider`         varchar(20)  NOT NULL,
    `provider_user_id` varchar(100) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);
