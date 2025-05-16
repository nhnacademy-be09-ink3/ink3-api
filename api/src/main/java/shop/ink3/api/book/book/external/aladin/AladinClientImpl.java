package shop.ink3.api.book.book.external.aladin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookDto;

@Component
@RequiredArgsConstructor
public class AladinClientImpl implements AladinClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private static final String TTB_KEY = "ttbin20151747001";
    private static final String BASE_URL = "https://www.aladin.co.kr/ttb/api/ItemLookUp.aspx";

    @Override
    public AladinBookDto fetchBookByIsbn(String isbn13) {
        String url = BASE_URL +
                "?ttbkey=" + TTB_KEY +
                "&itemIdType=ISBN13" +
                "&ItemId=" + isbn13 +
                "&output=JS" +
                "&Version=20131101" +
                "&OptResult=toc,fulldescription";

        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode item = root.path("item").get(0);

            return new AladinBookDto(
                    item.path("title").asText(),
                    item.path("description").asText(null),
                    item.path("toc").asText(null),
                    item.path("author").asText(null),
                    item.path("publisher").asText(null),
                    item.path("pubDate").asText(null),
                    item.path("isbn13").asText(null),
                    item.path("priceStandard").asInt(),
                    item.path("priceSales").asInt(),
                    item.path("cover").asText(null),
                    item.path("categoryName").asText(null)
            );
        } catch (Exception e) {
            throw new RuntimeException("알라딘 API 파싱 실패", e);
        }
    }
}
