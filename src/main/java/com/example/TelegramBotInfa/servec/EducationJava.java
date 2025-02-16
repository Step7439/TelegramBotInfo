package com.example.TelegramBotInfa.servec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.example.TelegramBotInfa.model.Title;
import com.example.TelegramBotInfa.repo.RepoTitle;
import com.vdurmont.emoji.EmojiParser;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EducationJava {
    
        String NEXT = "NEXT_JAVA";
        String str;
        private final long JAVA_ID = 57;
        @Autowired
        public RepoTitle repoTitle;
        TelegramBot telegramBot;
        
        public SendMessage nextJava(long chatId) {
            Title title = getRandomJava().orElseThrow(() -> new EntityNotFoundException("No titles found for Java"));
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(title.getBodyJava());
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> listButton = new ArrayList<>();
            List<InlineKeyboardButton> buttons = new ArrayList<>();
    
            var titleButton = new InlineKeyboardButton();
    
             titleButton.setText(EmojiParser.parseToUnicode("Следущий" + " \uD83E\uDD16"));
                 titleButton.setCallbackData(NEXT);


        buttons.add(titleButton);

        listButton.add(buttons);
        markup.setKeyboard(listButton);
        message.setReplyMarkup(markup);
        
        return message;
        
        
    }
     private Optional<Title> getRandomJava() {
        var r = new Random();
        var randomId = r.nextLong(JAVA_ID) + 1;
        return repoTitle.findById(randomId);
    }

 }
