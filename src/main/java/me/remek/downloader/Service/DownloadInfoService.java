package me.remek.downloader.Service;

import me.remek.downloader.Model.DownloadInfo;
import me.remek.downloader.Model.Users;
import me.remek.downloader.Repository.DownloadInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DownloadInfoService {
    private final DownloadInfoRepository downloadInfoRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    public DownloadInfoService(DownloadInfoRepository downloadInfoRepository) {
        this.downloadInfoRepository = downloadInfoRepository;
    }

    public void saveDownloadInfo(DownloadInfo downloadInfo) {
        downloadInfoRepository.save(downloadInfo);
    }

    public List<DownloadInfo> getAllDownloadInfo() {

        Users u = usersService.getLoggedUser();

        downloadInfoRepository.findAll(u).forEach(x -> System.out.println(x.getFileUrl()));;

        return downloadInfoRepository.findAll(u);
    }

    public DownloadInfo getDownloadInfoById(Long id) {
        return downloadInfoRepository.findById(id).orElse(null);
    }

    public void deleteDownloadInfo(Long id) {
        Optional<DownloadInfo> di = downloadInfoRepository.findById(id);
        di.ifPresent(downloadInfoRepository::delete);
    }
}
