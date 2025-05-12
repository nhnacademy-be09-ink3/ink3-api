package shop.ink3.api.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import shop.ink3.api.books.authors.entity.Authors;

public interface AuthorsRepository extends JpaRepository<Authors, Long> {
    Optional<List<Authors>> getAllByName(String name);

    ///test 용 임시 파일
}