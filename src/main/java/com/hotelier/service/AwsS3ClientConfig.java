package com.hotelier.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.logging.Logger;

@Configuration
public class AwsS3ClientConfig {
    static String bucketName ="hotelier";
    @Value("${cloud.aws.credentials.accessKey:}")
    private String awsId;

    @Value("${cloud.aws.credentials.secretKey:}")
    private String awsKey;

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Bean
    public S3Client s3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create(awsId, awsKey);
        S3Client build = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        if(!doesBucketExist(bucketName, build)){
            logger.info("creating bucket...");
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            build.createBucket(createBucketRequest);
        }
        return build;
    }

    public boolean doesBucketExist(String bucketName, S3Client s3Client) {
        HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            s3Client.headBucket(headBucketRequest);
            return true;  // Bucket exists
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;  // Bucket does not exist
            } else {
                throw e;  // Rethrow the exception for other errors
            }
        }
    }

}