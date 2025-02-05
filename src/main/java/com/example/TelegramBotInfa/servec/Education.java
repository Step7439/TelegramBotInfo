package com.example.TelegramBotInfa.servec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.TelegramBotInfa.config.ConfigBot;
import com.example.TelegramBotInfa.model.Title;
import com.example.TelegramBotInfa.repo.RepoTitle;
import com.vdurmont.emoji.EmojiParser;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class Education extends TelegramBot {

    public Education(ConfigBot configBot) {
        super(configBot);
        //TODO Auto-generated constructor stub
    }
    String NEXT = "";
    String str;
    private final long JAVA_ID = 57;
    private RepoTitle repoTitle;
    
    public void javaNext(String title, long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(title);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listButton = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        var titleButton = new InlineKeyboardButton();

         titleButton.setText(EmojiParser.parseToUnicode("Следущий" + " \uD83E\uDD16"));
             titleButton.setCallbackData(NEXT);

//         if (NEXT == NEXT_ENG) {
//             titleButton.setText(EmojiParser.parseToUnicode("Следущий" + " \uD83C\uDDF1\uD83C\uDDF7"));
//             titleButton.setCallbackData(NEXT);
//         } else {
//             titleButton.setText(EmojiParser.parseToUnicode("Следущий" + " \uD83E\uDD16"));
//             titleButton.setCallbackData(NEXT);
//         }

        buttons.add(titleButton);

        listButton.add(buttons);
        markup.setKeyboard(listButton);
        message.setReplyMarkup(markup);

        executeMessag(message);
    }

     public void engNext(String title, String trans, long chatId) throws TelegramApiException {
        str = trans;
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(title + " " +  " ||" + trans + "||");
        message.setParseMode("MarkdownV2");

        executeMessag(message);
}

    public Optional<Title> getRandomJava() {
        var r = new Random();
        var randomId = r.nextLong(JAVA_ID) + 1;
        return repoTitle.findById(randomId);
    }
 }
