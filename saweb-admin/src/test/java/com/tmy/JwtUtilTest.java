package com.tmy;

import com.tmy.common.utils.JwtUtil;
import com.tmy.sys.entity.User;
import io.jsonwebtoken.Claims;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtUtilTest {
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testCreateJwt(){
        User user = new User();
        user.setUsername("zhangsan");
        user.setPhone("13966667777");
        user.getPhone();
        String token = jwtUtil.createToken(user);
        System.out.println(token);
    }

    @Test
    public void testParseJwt(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiZDA4ZDdlOS1lZTY0LTRmYjUtYTYzMi0wNWIzMzZlZTEyMWEiLCJzdWIiOiJ7XCJwaG9uZVwiOlwiMTM5NjY2Njc3NzdcIixcInVzZXJuYW1lXCI6XCJ6aGFuZ3NhblwifSIsImlzcyI6InN5c3RlbSIsImlhdCI6MTcwNzgyODA0MSwiZXhwIjoxNzA3ODI5ODQxfQ.B8GPkxwXaf3n55-IugqCOlvcFHia56gFwHiEVreqrXE";
        Claims claims = jwtUtil.parseToken(token);
        System.out.println(claims);
    }
}
