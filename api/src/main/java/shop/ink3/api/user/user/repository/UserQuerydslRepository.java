package shop.ink3.api.user.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.ink3.api.user.user.dto.UserListItemDto;
import shop.ink3.api.user.user.dto.UserStatisticsResponse;

public interface UserQuerydslRepository {
    Page<UserListItemDto> getUsersForManagement(String keyword, Pageable pageable);
    
    UserStatisticsResponse getUserStatistics();
}
