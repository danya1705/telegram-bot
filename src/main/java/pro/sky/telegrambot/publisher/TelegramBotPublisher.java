package pro.sky.telegrambot.publisher;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;

@Service
public class TelegramBotPublisher {

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskService notificationTaskService;

    @Scheduled(cron = "0 0/1 * * * *")
    public void checkForNotifications() {

        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> notificationList = notificationTaskService.checkNotifications(time);

        Iterator<NotificationTask> iterator = notificationList.iterator();
        NotificationTask task;
        while (iterator.hasNext()) {
            task = iterator.next();
            printNotification(task);
            notificationTaskService.removeNotification(task.getId());
        }
    }

    private void printNotification(NotificationTask task) {
        String text = task.getText();
        long chatId = task.getChatId();
        SendMessage message = new SendMessage(chatId, "<b>" + text + "</b>\nУдачи!");
        message.parseMode(ParseMode.HTML);
        SendResponse response = telegramBot.execute(message);
    }

}
