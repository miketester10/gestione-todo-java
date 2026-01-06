package com.example.dataware.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.dataware.todolist.s3.S3Properties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {

        private final S3Properties S3Properties;

        @Bean
        public S3Client S3Client() {

                AwsBasicCredentials credentials = AwsBasicCredentials.create(
                                S3Properties.getAccessKeyId(),
                                S3Properties.getSecretAccessKey());

                return S3Client.builder()
                                .credentialsProvider(
                                                StaticCredentialsProvider.create(credentials))
                                .region(Region.of(S3Properties.getRegion()))
                                .build();
        }

}
