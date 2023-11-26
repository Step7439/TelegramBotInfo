package com.example.TelegramBotInfa.servec;

import com.example.TelegramBotInfa.config.ConfigBot;
import com.example.TelegramBotInfa.model.Eng;
import com.example.TelegramBotInfa.model.Title;
import com.example.TelegramBotInfa.model.Users;
import com.example.TelegramBotInfa.repo.RepoEng;
import com.example.TelegramBotInfa.repo.RepoTitle;
import com.example.TelegramBotInfa.repo.RepoUsers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
import java.util.*;
import java.io.File;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    final ConfigBot configBot;
    @Autowired
    public RepoUsers repoUser;
    @Autowired
    private RepoTitle repoTitle;
    @Autowired
    private RepoEng repoEng;
    private final String NEXT_JAVA = "NEXT_JAVA";
    private final String NEXT_ENG = "NEXT_ENG";
    String NEXT = "";
    private final long TITLE_ID = 14;

    public TelegramBot(ConfigBot configBot) {
        this.configBot = configBot;
        menuBot();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messagText = update.getMessage().getText();
            ReplyKeyboardMarkup keyboardMac = new ReplyKeyboardMarkup();
            long chatId = update.getMessage().getChatId();

            switch (messagText) {
                case "/start" -> {
                    try {
                        registUser(update.getMessage());
                    } catch (TelegramApiException e) {
                        log.error("Error Register user " + e.getMessage());
                    }
                    log.info("register user " + update.getMessage().getChat().getUserName());
                    startCommand(chatId, update.getMessage().getChat().getFirstName());
                }
                case "/education" -> {
                    sendMessageReply(keyboardMac, chatId, messagText);
                    fileAddJava();
                    fileAddEng();
                }
                case "/java" -> {
                    NEXT = NEXT_JAVA;
                    var title = getRandomJava();
                    title.
                            ifPresent(valueBody -> titleNextButtonMenu(valueBody.getBodyJava(), chatId));
                }
                case "/english" -> {
                    NEXT = NEXT_ENG;
                    var eng = getRandomEng();
                    eng.ifPresent(valueEng -> titleNextButtonMenu(valueEng.getBodyEng(), chatId));
                }
                default -> sendMesseg(chatId, "Я еще маленький и не все знаю!");
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {

                case NEXT_JAVA -> {
                    var title = getRandomJava();
                    title.ifPresent(valueBody -> titleNextButtonMenu(valueBody.getBodyJava(), chatId));
                }
                case NEXT_ENG -> {
                    var engs = getRandomEng();
                    engs.ifPresent(valueEng -> titleNextButtonMenu(valueEng.getBodyEng(), chatId));
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return configBot.getName();
    }

    @Override
    public String getBotToken() {
        return configBot.getToken();
    }

    private void sendMesseg(long chatId, String text) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        executeMessag(message);
    }

    private void sendMessageReply(ReplyKeyboardMarkup markup, long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        markup.setKeyboard(menuKeyboard());
        message.setReplyMarkup(markup);

        executeMessag(message);
    }

    private List<KeyboardRow> menuKeyboard() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        var row = new KeyboardRow();

        row.add("/java");
        row.add("/english");
        keyboardRows.add(row);
        return keyboardRows;
    }

    private void fileAddJava() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<Title> titleList = objectMapper.readValue(new File("db/title.json"),
                    typeFactory.constructCollectionType(List.class, Title.class));
            repoTitle.saveAll(titleList);
        } catch (Exception e) {
            log.error("Error File Title" + e.getMessage());
        }
    }

    private void fileAddEng() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<Eng> titleList = objectMapper.readValue(new File("db/eng.json"),
                    typeFactory.constructCollectionType(List.class, Eng.class));
            repoEng.saveAll(titleList);
        } catch (Exception e) {
            log.error("Error File Eng" + e.getMessage());
        }
    }

    private void menuBot() {
        List<BotCommand> list = new ArrayList<>();
        list.add(new BotCommand("/start", "Погнали заниматься"));
        list.add(new BotCommand("/education", "Изучаем"));

        try {
            this.execute(new SetMyCommands(list, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error BotComand" + e.getMessage());
        }
    }

    private void executeMessag(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error :" + e.getMessage());
        }
    }

    private void startCommand(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Привет " + name + ", молодец что ты с нами " + "\uD83D\uDE0A" + ":blush: ");
        sendMesseg(chatId, answer);
    }

    private void registUser(Message message) throws TelegramApiException {
        if (repoUser.findById(message.getChatId()).isEmpty()) {
            var chatId = message.getChatId();
            var chat = message.getChat();
            Users users = new Users();
            users.setId(chatId);
            users.setName(chat.getUserName());
            users.setTimestamp(new Timestamp(System.currentTimeMillis()));
            repoUser.save(users);
        }
    }

    private void titleNextButtonMenu(String title, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(title);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listButton = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        var titleButton = new InlineKeyboardButton();

        if (NEXT == NEXT_ENG) {
            titleButton.setText(EmojiParser.parseToUnicode("Следущий" + " \uD83C\uDDF1\uD83C\uDDF7"));
            titleButton.setCallbackData(NEXT);
        } else {
            titleButton.setText(EmojiParser.parseToUnicode("Следущий" + " \uD83E\uDD16"));
            titleButton.setCallbackData(NEXT);
        }

        buttons.add(titleButton);

        listButton.add(buttons);
        markup.setKeyboard(listButton);
        message.setReplyMarkup(markup);

        executeMessag(message);
    }

    private void titleNextKeyboard(String title, String messegeText, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(title);


        executeMessag(message);
        while (true){
        if(title.equals(messegeText)){
            message.setText("ENG");
            break;
        }else {
            sendMesseg(chatId, "Не правельно!");
        }}
    }

    private Optional<Title> getRandomJava() {
        var r = new Random();
        var randomId = r.nextLong(TITLE_ID) + 1;
        return repoTitle.findById(randomId);
    }

    private Optional<Eng> getRandomEng() {
        var r = new Random();
        var randomId = r.nextLong(TITLE_ID) + 1;
        return repoEng.findById(randomId);
    }
}