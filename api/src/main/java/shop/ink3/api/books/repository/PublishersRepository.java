package shop.ink3.api.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.books.entity.Publishers;

public interface PublishersRepository extends JpaRepository<Publishers, Long> {
}
