package com.example.dataware.todolist.filter.rateLimiter.enums;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpMethod;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateLimitEndpoint {

    REGISTER(HttpMethod.POST, "/auth/register", 4, 60),
    LOGIN(HttpMethod.POST, "/auth/login", 4, 60),
    LOGOUT(HttpMethod.DELETE, "/auth/logout", 4, 60),
    REFRESH_TOKEN(HttpMethod.POST, "/auth/refresh-token", 4, 60),
    UPLOAD_IMAGE(HttpMethod.POST, "/users/profile/image", 2, 60),
    DELETE_IMAGE(HttpMethod.DELETE, "/users/profile/image", 2, 60);

    private final HttpMethod method;
    private final String path;
    private final int maxRequests;
    private final long windowSeconds;

    public static Optional<RateLimitEndpoint> from(HttpServletRequest request) {
        return Arrays.stream(RateLimitEndpoint.values())
                .filter(value -> value.method.matches(request.getMethod())
                        && value.path.equals(request.getRequestURI()))
                .findFirst();
    }

}
