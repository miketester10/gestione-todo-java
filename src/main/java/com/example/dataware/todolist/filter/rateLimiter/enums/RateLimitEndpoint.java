package com.example.dataware.todolist.filter.rateLimiter.enums;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateLimitEndpoint {

    REGISTER("/auth/register", 4, 60),
    LOGIN("/auth/login", 4, 60),
    LOGOUT("/auth/logout", 4, 60),
    REFRESH_TOKEN("/auth/refresh-token", 4, 60),
    UPLOAD_IMAGE("/users/profile/image", 2, 60);

    private final String path;
    private final int maxRequests;
    private final long windowSeconds;

    public static Optional<RateLimitEndpoint> fromPath(String path) {
        return Arrays.stream(RateLimitEndpoint.values())
                .filter(value -> value.path.equals(path))
                .findFirst();
    }

}
