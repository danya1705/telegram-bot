package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskService notificationTaskService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            long chatId = update.message().chat().id();

            if (update.message().text().equals("/start")) {
                printMessage(chatId, "Hello, " + update.message().from().firstName() + "!");
            } else {
                Optional<NotificationTask> task = notificationTaskService.addNotification(chatId, update.message().text());
                if (task.isPresent()) {
                    printSuccessMessage(chatId, task.get());
                } else {
                    printMessage(chatId, "Неправильный формат. Сообщение должно быть вида:" +
                            "\n<i>01.01.2023 20:00 Текст напоминания</i>");
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void printMessage(long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        message.parseMode(ParseMode.HTML);
        SendResponse response = telegramBot.execute(message);
    }

    private void printSuccessMessage(long chatId, NotificationTask task) {
        LocalDate date = task.getTime().toLocalDate();
        LocalTime time = task.getTime().toLocalTime();
        String text = task.getText();
        SendMessage message = new SendMessage(chatId,
                "Напоминание \"" + text + "\" успешно добавлено" +
                ".\nОно активируется " + date + " в " + time +
                ".\nСпасибо, что воспользовались нашим сервисом!");
        SendResponse response = telegramBot.execute(message);
    }

}
