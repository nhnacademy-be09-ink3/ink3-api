package shop.ink3.api.common.uploader;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.ink3.api.common.exception.MinioUploadFailException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUploader {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public String upload(MultipartFile file, String bucket) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = currentDate + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.warn("upload======key={}", key);

            return key;
        } catch (IOException e) {
            throw new MinioUploadFailException("MinIO 파일 업로드 실패");
        }
    }

    public String getPresignedUrl(String objectName, String bucket) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(objectName)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60))
            .getObjectRequest(getObjectRequest)
            .build();

        URL url = s3Presigner.presignGetObject(presignRequest).url();
        log.warn("get==========url={}", url.toString());
        return url.toString();
    }

    public void delete(String objectName, String bucket) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(objectName)
            .build());
    }
}
