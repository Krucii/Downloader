package me.remigiuszkatrynski.downloader.Service;

import me.remigiuszkatrynski.downloader.Model.DownloadInfo;
import me.remigiuszkatrynski.downloader.Repository.DownloadInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class DownloadInfoService {
    private final DownloadInfoRepository downloadInfoRepository;

    @Autowired
    public DownloadInfoService(DownloadInfoRepository downloadInfoRepository) {
        this.downloadInfoRepository = downloadInfoRepository;
    }

    public DownloadInfo saveDownloadInfo(DownloadInfo downloadInfo) {
        return downloadInfoRepository.save(downloadInfo);
    }

    public List<DownloadInfo> getAllDownloadInfo() {
        return downloadInfoRepository.findAll();
    }

    public DownloadInfo getDownloadInfoById(Long id) {
        return downloadInfoRepository.findById(id).orElse(null);
    }

    public long getDownloadTotalSize(DownloadInfo downloadInfo) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(downloadInfo.getFileUrl()))
                .header("Range", "bytes=0-")
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
            String contentRange = response.headers().firstValue("Content-Range").orElse(null);

            if (contentRange != null) {
                String[] parts = contentRange.split("/");
                if (parts.length > 1) {
                    return Long.parseLong(parts[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
