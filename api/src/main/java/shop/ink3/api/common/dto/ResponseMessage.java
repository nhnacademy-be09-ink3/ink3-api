package shop.ink3.api.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseMessage {
    SUCCESS("Request processed successfully."),
    CREATED("Resource created successfully."),
    UPDATED("Resource updated successfully.");

    private final String message;
}
