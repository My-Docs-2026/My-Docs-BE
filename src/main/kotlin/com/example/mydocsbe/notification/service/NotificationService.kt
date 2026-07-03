package com.example.mydocsbe.notification.service

import com.example.mydocsbe.docs.repository.UploadDocsRepository
import com.example.mydocsbe.notification.dto.response.NotificationResponse
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class NotificationService(
    private val uploadDocsRepository: UploadDocsRepository,
) {
    fun getNotifications(userId: String): List<NotificationResponse> {
        val after = Instant.now().minusSeconds(86400)
        return uploadDocsRepository
            .findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, after)
            .map { doc ->
                NotificationResponse(
                    id = doc.id.toString(),
                    title = doc.title,
                    time = doc.createdAt.toString(),
                    status = doc.status.name,
                )
            }
    }
}
