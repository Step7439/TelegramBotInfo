package com.example.TelegramBotInfa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Users {
    @Id
    private Long id;
    private String name;
    private Timestamp timestamp;

}
