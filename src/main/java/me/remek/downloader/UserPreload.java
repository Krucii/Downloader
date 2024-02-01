package me.remek.downloader;

import me.remek.downloader.Model.Stats;
import me.remek.downloader.Model.Users;
import me.remek.downloader.Repository.StatsRepository;
import me.remek.downloader.Repository.UsersRepository;
import me.remek.downloader.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class UserPreload {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    StatsRepository statsRepository;

    @Bean
    CommandLineRunner loadBasicUser() {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
            Users u = new Users(1L, null, "admin", encoder.encode("admin"), true, new Stats());
            Users u2 = new Users(2L, null, "user", encoder.encode("user"), false, new Stats());

            usersRepository.save(u);
            usersRepository.save(u2);
        };
    }
}
