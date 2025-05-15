package shop.ink3.api.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.point.PointPolicy;

import java.time.LocalDateTime;

public class PointPolicyDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "정책 이름은 필수입니다.")
        @Size(min = 1, max = 20, message = "정책 이름은 1~20자 사이여야 합니다.")
        private String name;
        
        @NotNull(message = "적립 포인트는 필수입니다.")
        @Min(value = 0, message = "적립 포인트는 0 이상이어야 합니다.")
        private Integer earnPoint;
        
        private Boolean isAvailable;
        

        
        @Min(value = 0, message = "만료 기간은 0 이상이어야 합니다.")
        private Integer expireMonths;

        public static Request from(PointPolicy pointPolicy) {
            return Request.builder()
                    .name(pointPolicy.getName())
                    .earnPoint(pointPolicy.getEarnPoint())
                    .isAvailable(pointPolicy.getIsAvailable())

                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private Integer earnPoint;
        private LocalDateTime createdAt;
        private Boolean isAvailable;

        private Integer expireMonths;

        public static Response from(PointPolicy pointPolicy) {
            return Response.builder()
                    .id(pointPolicy.getId())
                    .name(pointPolicy.getName())
                    .earnPoint(pointPolicy.getEarnPoint())
                    .createdAt(pointPolicy.getCreatedAt())
                    .isAvailable(pointPolicy.getIsAvailable())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @Size(max = 20, message = "정책 이름은 최대 20자까지 가능합니다.")
        private String name;
        
        @Min(value = 0, message = "적립 포인트는 0 이상이어야 합니다.")
        private Integer earnPoint;
        
        private Boolean isAvailable;
        

        
        @Min(value = 0, message = "만료 기간은 0 이상이어야 합니다.")
        private Integer expireMonths;
    }
}
