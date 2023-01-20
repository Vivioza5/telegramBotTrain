package com.example.demo.trainingbot.service;

import com.example.demo.trainingbot.config.BotConfig;
import com.example.demo.trainingbot.enums.ButtonNameEnum;
import com.example.demo.trainingbot.enums.CommandNameEnum;
import com.example.demo.trainingbot.model.User;
import com.example.demo.trainingbot.model.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TrainingBot extends TelegramLongPollingBot {
    public static final String YES_BUTTON = "YES_BUTTON";
    public static final String NO_BUTTON = "NO_BUTTON";
    @Autowired
    private UserRepository userRepository;
    final BotConfig config;
    static final String HELP_TEXT =
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

        listOfCommands.add(new BotCommand(CommandNameEnum.START_COMMAND.getCommandName(), "command to start talk"));
        listOfCommands.add(new BotCommand(CommandNameEnum.HELP_COMMAND.getCommandName(), "command to call help"));
        listOfCommands.add(new BotCommand(CommandNameEnum.GET_MY_DATA_COMMAND.getCommandName(), "command to show my data"));
        listOfCommands.add(new BotCommand(CommandNameEnum.DELETE_MY_DATA_COMMAND.getCommandName(), "command to delete my data"));
        listOfCommands.add(new BotCommand(CommandNameEnum.SETTINGS_COMMAND.getCommandName(), "command to change settings of the bot"));
        listOfCommands.add(new BotCommand(CommandNameEnum.REGISTER_COMMAND.getCommandName(), "command to change settings of the bot"));
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
            if(messageText.contains(CommandNameEnum.SEND_MESSAGE_TO_ALL_COMMAND.getCommandName())&&chatId==config.getBOT_OWNER()){

                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user :
                        users) {
                    sendMessage(user.getChatid(), textToSend);
                }
            }

            switch (messageText) {
                case ("/start"):
                    startCommandRecived(chatId, update.getMessage().getChat().getFirstName());
                    registerUser(update.getMessage());
                    break;
                case ("/help"):
                    sendMessage(chatId, HELP_TEXT, getReplyIgnoreMessKeyboardMarkup());
                    break;
                case ("/register"):
                    register(chatId);
                    break;
                default:
                    sendMessage(chatId, "Sorry this command no recognised", getReplyKeyboardMarkup());
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callbackData.equals(YES_BUTTON)) {
                String message = "you press button Yes";
                editMessageText((int) messageId, chatId, message);


            } else if (callbackData.equals(NO_BUTTON)) {
                String message = "you press button No";
                editMessageText((int) messageId, chatId, message);
            }
        }
    }

    private void editMessageText(int messageId, long chatId, String message) {
        EditMessageText messageText = new EditMessageText();
        messageText.setChatId(chatId);
        messageText.setText(message);
        messageText.setMessageId(messageId);
        try {
            execute(messageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void register(long chatId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("DO you realy want to register?");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText(ButtonNameEnum.YES_BUTTON.getButtonName());
        yesButton.setCallbackData(YES_BUTTON);
        var noButton = new InlineKeyboardButton();
        noButton.setText(ButtonNameEnum.NO_BUTTON.getButtonName());
        noButton.setCallbackData(NO_BUTTON);
        rowInline.add(yesButton);
        rowInline.add(noButton);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sm.setReplyMarkup(markupInline);
        executeMessage(sm);
    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
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
        String answer = EmojiParser.parseToUnicode("Hi, " + firstName + " what next command?" + ":blush:");
        sendMessage(chatId, answer, getReplyKeyboardMarkup());
    }

    private void sendMessage(long chatId, String text, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage sm = new SendMessage();
        sm.setText(text);
        sm.setChatId(chatId);
        sm.setReplyMarkup(keyboardMarkup);
        executeMessage(sm);
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
            e.printStackTrace();
        }
    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("weather");
        row.add("get random joke");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private static ReplyKeyboardMarkup getReplyIgnoreMessKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("register");
        row.add("check my data");
        row.add("delete my data");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
