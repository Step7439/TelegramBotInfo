package com.example.TelegramBotInfa.servec;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.example.TelegramBotInfa.model.Eng;
import com.example.TelegramBotInfa.repo.RepoEng;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EducationEnglish {
    String str;
    private final long ENG_ID = 100;
    @Autowired
    private RepoEng repoEng;


     SendMessage nextEng(long chatId) {
        Eng eng = getRandomEng().orElseThrow(() -> new EntityNotFoundException("No titles found for Java"));
        String trans = eng.getTransl();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(eng.getBodyEng());
        str = trans;
        return message;

    }

    private Optional<Eng> getRandomEng() {
        var r = new Random();
        var randomId = r.nextLong(ENG_ID) + 1;
        return repoEng.findById(randomId);
    }
}
