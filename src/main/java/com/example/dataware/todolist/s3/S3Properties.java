package com.example.dataware.todolist.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "s3.aws")
public class S3Properties {
    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private String S3Bucket;
    private String defaultAvatarUrl;

}
