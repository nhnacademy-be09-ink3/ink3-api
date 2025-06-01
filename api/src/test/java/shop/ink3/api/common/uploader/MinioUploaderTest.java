// package shop.ink3.api.common.uploader;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.io.IOException;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.MockitoAnnotations;
// import org.springframework.mock.web.MockMultipartFile;
//
// import shop.ink3.api.common.exception.MinioUploadFailException;
//
// class MinioUploaderTest {
//     @InjectMocks
//     private MinioUploader uploader;
//
//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }
//
//     @Test
//     @DisplayName("Minio 이미지 업로드 성공")
//     void uploadSuccess() throws IOException {
//         byte[] content = "test image".getBytes();
//         MockMultipartFile file = new MockMultipartFile(
//             "file", "test.jpg", "image/jpeg", content
//         );
//
//         String result = uploader.upload(file, "review-bucket", "reviews");
//
//         assertThat(result).contains("reviews/");
//         assertThat(result).contains("test.jpg");
//     }
//
//     @Test
//     @DisplayName("Minio 이미지 업로드 실패")
//     void uploadFail() throws IOException {
//         MockMultipartFile file = mock(MockMultipartFile.class);
//         when(file.getOriginalFilename()).thenReturn("test.jpg");
//         when(file.getContentType()).thenReturn("image/jpeg");
//         when(file.getInputStream()).thenThrow(IOException.class);
//
//         assertThatThrownBy(() -> uploader.upload(file, "bucket", "dir"))
//             .isInstanceOf(MinioUploadFailException.class)
//             .hasMessageContaining("MinIO 파일 업로드 실패");
//     }
// }
