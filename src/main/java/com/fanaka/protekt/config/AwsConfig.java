package com.fanaka.protekt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.localstack.endpoint:http://localhost:4566}")
    private String localStackEndpoint;

    // Production S3Client - uses AWS credentials from environment/IAM roles
    @Bean
    @Profile("!localhost")
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    @Profile("localhost")
    public S3Client localStackS3Client() {
        // For LocalStack, use dummy credentials
        AwsBasicCredentials credentials = AwsBasicCredentials.create("test", "test");
        System.out.println("Creating LocalStack S3Client with endpoint: " + localStackEndpoint + ", region: " + awsRegion);
        return S3Client.builder()
                .endpointOverride(URI.create(localStackEndpoint))
                .region(Region.of(awsRegion))
                .forcePathStyle(true)  // Required for LocalStack
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    // Production S3Presigner - uses AWS credentials from environment/IAM roles  
    @Bean
    @Profile("!localhost")
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    @Profile("localhost")
    public S3Presigner localStackS3Presigner() {
        // For LocalStack, use dummy credentials
        AwsBasicCredentials credentials = AwsBasicCredentials.create("test", "test");
        
        // Configure S3 to use path-style access
        S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();
        
        return S3Presigner.builder()
                .endpointOverride(URI.create(localStackEndpoint))
                .region(Region.of(awsRegion))
                .serviceConfiguration(s3Config)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}