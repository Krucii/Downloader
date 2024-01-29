package me.remek.downloader.Controller;

import lombok.RequiredArgsConstructor;
import me.remek.downloader.Model.DownloadInfo;
import me.remek.downloader.Model.Stats;
import me.remek.downloader.Model.Users;
import me.remek.downloader.Service.DownloadInfoService;
import me.remek.downloader.Service.StatsService;
import me.remek.downloader.Service.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final DownloadInfoService service;
    private final UsersService usersService;
    private final StatsService statsService;

    @GetMapping("/")
    public String home(Model model) {
        List<DownloadInfo> downloadList = service.getAllDownloadInfo();

        Boolean isAdmin = usersService.getLoggedUser().getIsAdmin();

        Stats s = statsService.findAll();

        model.addAttribute("stats", s);
        model.addAttribute("downloadList", downloadList);
        model.addAttribute("isAdmin", isAdmin);
        return "home";
    }

}
