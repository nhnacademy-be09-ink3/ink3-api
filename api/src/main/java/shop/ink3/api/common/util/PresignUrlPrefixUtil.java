package shop.ink3.api.common.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 외부 API가 아닌 DB의 이미지를 띄우려고 할 때, http://storage.java21.net:8000/로 시작한다면
 * 해당 util 클래스를 사용해 reverse proxy 되게 해야 함
 * -> 이유 : 현재 http로 접근하는 url은 웹에서 보안 정책으로 인해 https로 업그레이드 하고 있음, 이 과정에서 오류 발생
 * -> 외부 API에서 가져온 이미지는 https로 시작할 것이기 때문에 해당 util 적용/미적용 뷴기가 필요함
 */
@Component
@Slf4j
public class PresignUrlPrefixUtil {

    private final Environment environment;

    private static final String PREFIX = "http://storage.java21.net:8000/";

    public PresignUrlPrefixUtil(Environment environment) {
        this.environment = environment;
    }

    public String addPrefixUrl(String presignedUrl) {
        if (presignedUrl == null || presignedUrl.isBlank()) {
            return presignedUrl;
        }

        List<String> profiles = Arrays.asList(environment.getActiveProfiles());

        if (profiles.contains("dev") && presignedUrl.startsWith(PREFIX)) {
            return "/dev/image-proxy/" + presignedUrl.substring(PREFIX.length());
        }

        if (profiles.contains("prod") && presignedUrl.startsWith(PREFIX)) {
            return "/image-proxy/" + presignedUrl.substring(PREFIX.length());
        }

        return presignedUrl;
    }
}

