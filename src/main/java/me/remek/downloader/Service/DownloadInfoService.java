package me.remek.downloader.Service;

import me.remek.downloader.Model.DownloadInfo;
import me.remek.downloader.Repository.DownloadInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DownloadInfoService {
    private final DownloadInfoRepository downloadInfoRepository;

    @Autowired
    public DownloadInfoService(DownloadInfoRepository downloadInfoRepository) {
        this.downloadInfoRepository = downloadInfoRepository;
    }

    public void saveDownloadInfo(DownloadInfo downloadInfo) {
        downloadInfoRepository.save(downloadInfo);
    }

    public List<DownloadInfo> getAllDownloadInfo() {
        return downloadInfoRepository.findAll();
    }

    public DownloadInfo getDownloadInfoById(Long id) {
        return downloadInfoRepository.findById(id).orElse(null);
    }
}
