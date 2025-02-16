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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
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
    RepoTitle repoTitle;
    @Autowired
    RepoEng repoEng;
    @Autowired
    RepoUsers repoUser;
    private final String NEXT_JAVA = "NEXT_JAVA";
    private final String NEXT_ENG = "NEXT_ENG";
    String NEXT = "";
    String messagText;
    String str;
    TelegramBot telegramBot;
    @Autowired
    EducationJava educationJava;
    @Autowired
    EducationEnglish english;
   
    @SuppressWarnings("deprecation")
    public TelegramBot(ConfigBot configBot) {
        this.configBot = configBot;
        menuBot();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            messagText = update.getMessage().getText();
            ReplyKeyboardMarkup keyboardMac = new ReplyKeyboardMarkup();
            keyboardMac.setResizeKeyboard(true);
            long chatId = update.getMessage().getChatId();
            //----------------Общие меню-------------------
            if (messagText.equals("/start")) {
                try {
                    registUser(update.getMessage());
                } catch (TelegramApiException e) {
                    log.error("Error Register user " + e.getMessage());
                }
                log.info("register user " + update.getMessage().getChat().getUserName());
                startCommand(chatId, update.getMessage().getChat().getFirstName());
            } else if (messagText.equals("/education")) {
                sendMessageReply(keyboardMac, chatId, messagText);
            } 
            else if (messagText.equals("/db")) {
                 fileAddJava();
                 fileAddEng();
            } 
            else if (messagText.equals("/java")) {
                 NEXT = NEXT_JAVA;
                
                try {
                    execute(educationJava.nextJava(chatId));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                
                } else if (messagText.equals("/english") || messagText.equals(str)) {
                sendMesseg(chatId, "Напечатай перевод");

                try {
                    execute(english.nextEng(chatId));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                
             } else {
                sendMesseg(chatId, "Not Found");
            }
        //----------------Работа с кнопками-----------------
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(NEXT_JAVA)) {
                try {
                    execute(educationJava.nextJava(chatId));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
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
    //-------------------Работа с клавиатурай--------------------
    private void sendMessageReply(ReplyKeyboardMarkup markup, long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        markup.setKeyboard(menuKeyboard());
        message.setReplyMarkup(markup);

        executeMessag(message);
    }
    //----------------Меню Education---------------------
    private List<KeyboardRow> menuKeyboard() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        var row = new KeyboardRow();

        row.add("/java");
        row.add("/english");
        keyboardRows.add(row);
        return keyboardRows;
    }
    //-----------------Работа с файломи Java------------------
    private void fileAddJava() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<Title> titleList = objectMapper.readValue(new File("/bot/db/title.json"),
                    typeFactory.constructCollectionType(List.class, Title.class));
            repoTitle.saveAll(titleList);
        } catch (Exception e) {
            log.error("Error File Title" + e.getMessage());
        }
    }
    //-----------------Работа с файломи Eng------------------
    private void fileAddEng() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<Eng> titleList = objectMapper.readValue(new File("/bot/db/eng.json"),
                    typeFactory.constructCollectionType(List.class, Eng.class));
            repoEng.saveAll(titleList);
        } catch (Exception e) {
            log.error("Error File Eng" + e.getMessage());
        }
    }
    //-----------------Создания меню------------------
    private void menuBot() {
        List<BotCommand> list = new ArrayList<>();
        list.add(new BotCommand("/start", "Регестрация"));
        list.add(new BotCommand("/education", "Изучаем"));

        try {
            this.execute(new SetMyCommands(list, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error BotComand" + e.getMessage());
        }
    }
    //------------------Отправка собщения--------------------
    public void executeMessag(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error :" + e.getMessage());
        }
    }
    //------------------Приветсвие------------------------
    private void startCommand(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Привет " + name + "\uD83D\uDE0A" + ":blush: ");
        sendMesseg(chatId, answer);
    }
    //--------------Регестрация и добовления user в базу-----------------
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

}