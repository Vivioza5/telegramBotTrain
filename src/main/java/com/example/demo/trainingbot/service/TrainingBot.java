package com.example.demo.trainingbot.service;

import com.example.demo.trainingbot.config.BotConfig;
import com.example.demo.trainingbot.enums.CommandNameEnum;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TrainingBot extends TelegramLongPollingBot {
    final BotConfig config;
    static final String HELP_TEXT =
            "Я бот, который поможет потренировать умения написания ботов\n\n" +
                    "❗*Список команд*\n" +
                    "/start - начала разговора\n" +
                    "/settings - просмотреть текущие настройки\n" +
                    "/help - помощь\n\n";

    public TrainingBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(CommandNameEnum.START_COMMAND.getCommandName(), "command to start talk"));
        listOfCommands.add(new BotCommand(CommandNameEnum.SETTINGS_COMMAND.getCommandName(), "command to change settings of the bot"));
        listOfCommands.add(new BotCommand(CommandNameEnum.HELP_COMMAND.getCommandName(), "command to change settings of the bot"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
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
            switch ( messageText ) {
                case ("/start") -> startCommandRecived(chatId, update.getMessage().getChat().getFirstName());
                case ("/help") -> sendMessage(chatId, HELP_TEXT);
                default -> sendMessage(chatId, "Sorry this command no recognised");
            }
        }
    }

    private void startCommandRecived(long chatId, String firstName) {
        SendMessage sm = new SendMessage();
        String answer = EmojiParser.parseToUnicode("Hi, " + firstName + " what next command?" + ":blush:");
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage sm = new SendMessage();
        sm.setText(text);
        sm.setChatId(chatId);
        executeMessage(sm);
    }

    private void executeMessage(SendMessage sm) {
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Error  bot " + e.getMessage());
        }
    }
}
