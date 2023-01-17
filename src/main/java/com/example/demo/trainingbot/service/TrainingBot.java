package com.example.demo.trainingbot.service;

import com.example.demo.trainingbot.config.BotConfig;
import com.example.demo.trainingbot.model.User;
import com.example.demo.trainingbot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TrainingBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    final BotConfig config;
    static final String HELP_TEXT=
            "Я бот, который поможет потренировать умения написания ботов\n\n" +
                        "❗*Список команд*\n" +
                        "/start - начала разговора\n" +
                        "/mydata - получение данных о пользователе\n" +
                        "/deletedata - удаление данных о пользователе\n" +
                        "/settings - просмотреть текущие настройки\n" +
                        "/help - помощь\n\n";

    public TrainingBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start","command to start talk"));
        listOfCommands.add(new BotCommand("/help","command to call help"));
        listOfCommands.add(new BotCommand("/mydata","command to show my data"));
        listOfCommands.add(new BotCommand("/deletedata","command to delete my data"));
        listOfCommands.add(new BotCommand("/settings","command to change settings of the bot"));
        try {
            this.execute( new SetMyCommands(listOfCommands, new BotCommandScopeDefault(),null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list" + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBOT_NAME();
    }

    @Override
    public String getBotToken() {
        return config.getBOT_TOKEN();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case ("/start"):
                    startCommandRecived(chatId, update.getMessage().getChat().getFirstName());
                    registerUser(update.getMessage());
                    break;
                case("/help"):
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId, "Sorry this command no recognised");
            }
        }
    }

    private void registerUser(Message msg) {
if (userRepository.findById(msg.getChatId()).isEmpty()){
    var chatId = msg.getChatId();
    var chat = msg.getChat();

    User user = new User();
    user.setChatid(chatId);
    user.setFirstName(chat.getFirstName());
    user.setLastName(chat.getLastName());
    user.setUserName(chat.getUserName());
    user.setStartTalk(new Timestamp(System.currentTimeMillis()));
    userRepository.save(user);
}
    }

    private void startCommandRecived(long chatId, String firstName) {
        SendMessage sm = new SendMessage();
        sm.setText("Hi, " + firstName + " what next command?");
        log.info("Replied to user" + firstName);
        sm.setChatId(chatId);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage sm = new SendMessage();
        sm.setText(text);
        sm.setChatId(chatId);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
