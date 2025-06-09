package shop.ink3.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import shop.ink3.api.common.uploader.MinioUploader;
import shop.ink3.api.common.util.PresignUrlPrefixUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class LogoController {
    private final MinioUploader minioUploader;
    private final PresignUrlPrefixUtil presignUrlPrefixUtil;

    @Value("${minio.logo-bucket}")
    private String logoBucket;

    @GetMapping("/logo-url")
    public String getLogoPresignedUrl() {
        String presignedUrl = minioUploader.getPresignedUrl("ink3_logo.png", logoBucket);
        return presignUrlPrefixUtil.addPrefixUrl(presignedUrl);
    }
}
