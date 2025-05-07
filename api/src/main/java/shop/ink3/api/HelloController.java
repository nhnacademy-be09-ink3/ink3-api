package shop.ink3.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(@RequestHeader(name = "X-MEMBER-ID", required = false) String memberId) {
        System.out.println("API 서버 도달! X-MEMBER-ID = " + memberId);
        return "X-MEMBER-ID: " + memberId;
    }
}
