package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final NotificationTaskService notificationTaskService;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            notificationTaskService.processUpdate(update);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotificationIfTime() {
        Collection<NotificationTask> notificationTasks = notificationTaskService.sendNotificationIfTime();
        System.out.println(notificationTasks);
        notificationTasks.forEach(notificationTask -> {
            telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getText()));
            notificationTaskService.updateStatus(notificationTask);
        });
    }

}
