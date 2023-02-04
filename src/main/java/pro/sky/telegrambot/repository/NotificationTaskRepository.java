package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.NotificationTask;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    @Query(value = "INSERT INTO notification_task (text, chat_id, send_time) VALUES (:text, :chat_id, :send_time) RETURNING id, text, chat_id, send_time, is_sent", nativeQuery = true)
    NotificationTask saveTask(String text, Long chat_id, LocalDateTime send_time);

    @Query(value = "SELECT id, text, chat_id, send_time, is_sent FROM notification_task WHERE send_time = date_trunc('minutes', now()) AND is_sent IS false", nativeQuery = true)
    Collection<NotificationTask> getNotificationTasksWhereSendTimeEqualsNow();
}

