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

        private final S3Properties s3Properties;

        @Bean
        public S3Client s3Client() {

                AwsBasicCredentials credentials = AwsBasicCredentials.create(
                                s3Properties.getAccessKeyId(),
                                s3Properties.getSecretAccessKey());

                return S3Client.builder()
                                .credentialsProvider(
                                                StaticCredentialsProvider.create(credentials))
                                .region(Region.of(s3Properties.getRegion()))
                                .build();
        }

}
