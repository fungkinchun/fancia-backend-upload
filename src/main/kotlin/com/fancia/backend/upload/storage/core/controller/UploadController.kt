package com.fancia.backend.upload.storage.core.controller

import com.fancia.backend.shared.upload.storage.core.dto.PresignUploadRequest
import com.fancia.backend.shared.upload.storage.core.dto.PresignUploadResponse
import com.fancia.backend.upload.storage.core.service.UploadService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/uploads")
@Tag(name = "Uploads", description = "Presigned S3 upload URLs")
@SecurityRequirement(name = "bearerAuth")
class UploadController(
    private val uploadService: UploadService,
) {
    @PostMapping("/presign")
    fun presignUpload(
        @RequestBody @Valid request: PresignUploadRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<PresignUploadResponse> {
        return ResponseEntity.ok(uploadService.presignUpload(request, jwt))
    }
}
