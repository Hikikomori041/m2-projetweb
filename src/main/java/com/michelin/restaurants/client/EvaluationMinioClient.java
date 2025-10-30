package com.michelin.restaurants.client;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class EvaluationMinioClient {

    private final MinioClient minioClient;
    private final String evaluationPhotoBucketName;

    public EvaluationMinioClient(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}") String secretKey,
            @Value("${evaluations.photo.bucketName}") String evaluationPhotoBucketName
    ) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        this.minioClient = io.minio.MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        this.evaluationPhotoBucketName = evaluationPhotoBucketName;

        log.info("Bucked coursm2 exists ? {}", this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(evaluationPhotoBucketName).build()));

        var x = this.minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .expiry(1000)
                .bucket(evaluationPhotoBucketName)
                .object("test.txt")
                .method(Method.POST)
                .build());

        log.info("Presigned URL: {}", x);
    }

    public String getEvaluationPhotoUpdateUrl(String photoName) {
        try {
            return this.minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs
                            .builder()
                            .bucket(evaluationPhotoBucketName)
                            .expiry(60)
                            .method(Method.PUT)
                            .object(photoName)
                            .build());
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEvaluationPhotoDeleteUrl(String photoName) {
        try {
            return this.minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs
                            .builder()
                            .bucket(evaluationPhotoBucketName)
                            .expiry(60)
                            .method(Method.DELETE)
                            .object(photoName)
                            .build());
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEvaluationPhotoUrl(String photoName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(evaluationPhotoBucketName)
                            .object(photoName)
                            .expiry(600)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'URL de lecture : " + e.getMessage(), e);
        }
    }

}
