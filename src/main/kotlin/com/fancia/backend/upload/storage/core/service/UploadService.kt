package com.fancia.backend.upload.storage.core.service

import com.fancia.backend.shared.common.core.exception.InvalidAuthenticationException
import com.fancia.backend.shared.upload.storage.core.dto.PresignUploadRequest
import com.fancia.backend.shared.upload.storage.core.dto.PresignUploadResponse
import com.fancia.backend.shared.upload.storage.core.enums.UploadPurpose
import com.fancia.backend.shared.upload.storage.core.enums.UploadScope
import com.fancia.backend.shared.upload.storage.core.exception.UploadDeniedException
import org.apache.commons.io.FilenameUtils
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.*

@Service
class UploadService(
    private val s3StorageService: S3StorageService,
) {
    fun presignUpload(request: PresignUploadRequest, jwt: Jwt): PresignUploadResponse {
        jwt.getClaimAsString("userId")?.let { UUID.fromString(it) }
            ?: throw InvalidAuthenticationException()
        validatePurpose(request.scope, request.purpose)
        val extension = FilenameUtils.getExtension(request.filename).ifBlank {
            request.contentType.extension
        }
        val objectKey = "tmp/${UUID.randomUUID()}.$extension"

        return s3StorageService.presignPutObject(objectKey, request.contentType)
    }

    private fun validatePurpose(scope: UploadScope, purpose: UploadPurpose) {
        if (purpose !in scope.allowedPurposes()) {
            throw UploadDeniedException()
        }
    }
}
