package me.remek.downloader.Controller;

import lombok.RequiredArgsConstructor;
import me.remek.downloader.Model.DownloadInfo;
import me.remek.downloader.Service.DownloadInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final DownloadInfoService service;

    @GetMapping("/")
    public String home(Model model) {
        List<DownloadInfo> downloadList = service.getAllDownloadInfo();
        model.addAttribute("downloadList", downloadList);
        return "home";
    }

}
