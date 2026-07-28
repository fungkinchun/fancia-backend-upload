package com.fancia.backend.upload.storage.core.service

import com.fancia.backend.shared.upload.storage.core.dto.PresignUploadResponse
import com.fancia.backend.shared.upload.storage.core.enums.ImageContentType
import com.fancia.backend.shared.upload.storage.s3.config.S3Configuration
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.time.Instant

@Service
class S3StorageService(
    private val s3Presigner: S3Presigner,
    private val s3Configuration: S3Configuration,
) {
    fun presignPutObject(objectKey: String, contentType: ImageContentType): PresignUploadResponse {
        val bucket = s3Configuration.bucketName
            ?: throw IllegalStateException("app.s3.bucket-name is not configured")
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(objectKey)
            .contentType(contentType.mimeType)
            .build()
        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .putObjectRequest(putObjectRequest)
            .build()
        val presigned = s3Presigner.presignPutObject(presignRequest)
        val expiresAt = Instant.now().plus(Duration.ofMinutes(15))

        return PresignUploadResponse(
            uploadUrl = presigned.url().toString(),
            objectKey = objectKey,
            expiresAt = expiresAt,
        )
    }
}
