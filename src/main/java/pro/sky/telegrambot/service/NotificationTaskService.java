package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public Optional<NotificationTask> addNotification(long chatId, String inputString) {

        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)(.+)");
        Matcher matcher = pattern.matcher(inputString);

        if (matcher.matches()) {
            String timeString = matcher.group(1);
            LocalDateTime time = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            String text = matcher.group(3);
            NotificationTask notificationTask = new NotificationTask(chatId, time, text);
            return Optional.of(notificationTaskRepository.save(notificationTask));
        } else {
            return Optional.empty();
        }
    }

    public List<NotificationTask> checkNotifications(LocalDateTime time) {
        return notificationTaskRepository.findAllByTime(time);
    }

    public void removeNotification(long id) {
        notificationTaskRepository.deleteById(id);
    }
}
