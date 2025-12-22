package com.example.dataware.todolist.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

}
