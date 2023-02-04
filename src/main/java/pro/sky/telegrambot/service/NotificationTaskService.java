package pro.sky.telegrambot.service;


import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskService {

    Logger logger = LoggerFactory.getLogger(NotificationTaskService.class);
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public void processUpdate(Update update) {
        logger.info("Save task to DB");
        String text = update.message().text();
        Long chatId = update.message().chat().id();

        Collection<String> textAndTime = findTaskTextAndTime(text);
        if (textAndTime.isEmpty()) {
            logger.error("No matches found in text: {}", text);
            return;
        }

        LocalDateTime taskDateTime;
        try {
            taskDateTime = LocalDateTime.parse(textAndTime.toArray()[1].toString(),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (Exception e) {
            logger.error("Wrong date format in text: {}", text);
            return;
        }

        String taskText = textAndTime.toArray()[0].toString();
        NotificationTask notificationTask = notificationTaskRepository.saveTask(taskText, chatId, taskDateTime);
        logger.info("Task saved to DB: {}", notificationTask);
    }

    public Collection<NotificationTask> sendNotificationIfTime() {
        logger.info("Send notification if time");
        return notificationTaskRepository.getNotificationTasksWhereSendTimeEqualsNow();
    }

    public void updateStatus(NotificationTask notificationTask) {
        logger.info("Update status for task: {}", notificationTask);
        notificationTask.setSent(true);
        notificationTaskRepository.save(notificationTask);
    }

    private Collection<String> findTaskTextAndTime(String text) {
        logger.info("Find task text and time");
        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return List.of(matcher.group(3), matcher.group(1));
        }
        return List.of();
    }
}
