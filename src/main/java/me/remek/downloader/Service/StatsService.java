package me.remek.downloader.Service;

import lombok.AllArgsConstructor;
import me.remek.downloader.Model.Stats;
import me.remek.downloader.Model.Users;
import me.remek.downloader.Repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StatsService {

    @Autowired
    private final StatsRepository statsRepository;

    private final UsersService usersService;

    public Stats findAll() {
        Users u = usersService.getLoggedUser(); // tu nie moze znalezc kto zalogowany
        return statsRepository.findAll(u);
    }

    public void save(Stats s) {
        statsRepository.save(s);
    }

    public void delete(Stats s) {
        statsRepository.delete(s);
    }

}
