package com.fooddelivery.ad.campaign.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@lombok.extern.slf4j.Slf4j
public class S3PresignedUrlService {

    private final S3Presigner presigner;
    private final String bucketName;

    public S3PresignedUrlService(
            @Value("${aws.s3.access-key:dummy-key}") String accessKey,
            @Value("${aws.s3.secret-key:dummy-secret}") String secretKey,
            @Value("${aws.s3.region:us-east-1}") String region,
            @Value("${aws.s3.bucket:fooddelivery-creatives}") String bucketName) {
        
        this.bucketName = bucketName;
        this.presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String generatePresignedUrl(UUID advertiserId, String fileName, String contentType) {
        if (!contentType.startsWith("image/") && !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("Invalid content type for ad creative");
        }
        
        String objectKey = String.format("upload/%s/%s_%s", advertiserId.toString(), UUID.randomUUID().toString(), fileName);
        
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        log.info("Generated presigned URL for advertiser {} with key {}", advertiserId, objectKey);
        
        return presignedRequest.url().toString();
    }
}
