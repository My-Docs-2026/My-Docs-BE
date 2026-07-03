package com.example.mydocsbe.notification.controller

import com.example.mydocsbe.notification.dto.response.NotificationResponse
import com.example.mydocsbe.notification.service.NotificationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {
    @GetMapping("/{userId}")
    fun getNotifications(
        @PathVariable userId: String,
    ): List<NotificationResponse> = notificationService.getNotifications(userId)
}
