package shop.ink3.api.book.book.external.aladin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import shop.ink3.api.book.book.exception.AladinBookNotFoundException;
import shop.ink3.api.book.book.exception.AladinParsingException;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookDto;

@Component
@RequiredArgsConstructor
public class AladinClientImpl implements AladinClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${aladin.ttb-key}")
    private String ttbKey;
    private static final String BASE_URL = "https://www.aladin.co.kr/ttb/api/ItemLookUp.aspx";

    @Override
    public AladinBookDto fetchBookByIsbn(String isbn13) {
        String url = BASE_URL +
                "?ttbkey=" + ttbKey +
                "&itemIdType=ISBN13" +
                "&ItemId=" + isbn13 +
                "&output=JS" +
                "&Version=20131101" +
                "&OptResult=toc,fulldescription";

        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("item");

            if (!items.isArray() || items.size() == 0) {
                throw new AladinBookNotFoundException(isbn13);
            }

            JsonNode item = items.get(0);

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
        } catch (AladinBookNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AladinParsingException(e);
        }
    }
}
