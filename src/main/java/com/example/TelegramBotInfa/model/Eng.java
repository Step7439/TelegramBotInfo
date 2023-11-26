package com.example.TelegramBotInfa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Eng {
    @Id
    Long id;
    String bodyEng;
}
