package com.example.TelegramBotInfa.repo;

import com.example.TelegramBotInfa.model.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RepoUsers extends CrudRepository<Users, Long> {
}
