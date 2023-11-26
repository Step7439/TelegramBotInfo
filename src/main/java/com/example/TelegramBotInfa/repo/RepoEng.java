package com.example.TelegramBotInfa.repo;

import com.example.TelegramBotInfa.model.Eng;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoEng extends CrudRepository<Eng, Long> {
}
