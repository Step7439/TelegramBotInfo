package com.example.TelegramBotInfa.repo;

import com.example.TelegramBotInfa.model.Title;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RepoTitle extends CrudRepository<Title, Long> {
}
