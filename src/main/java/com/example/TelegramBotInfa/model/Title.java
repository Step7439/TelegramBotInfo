package com.example.TelegramBotInfa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
@Entity
@Data
public class Title {
    @Id
    Long id;
    @Column(length = 1000)
    String bodyJava;
}
