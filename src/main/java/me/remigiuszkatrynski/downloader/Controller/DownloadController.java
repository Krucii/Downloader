package me.remigiuszkatrynski.downloader.Controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.remigiuszkatrynski.downloader.Model.DownloadInfo;
import me.remigiuszkatrynski.downloader.Service.DownloadInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@Controller
@RequestMapping("/downloads")
public class DownloadController {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class DownloadRequest {
        private String url;
        private String dest;
    }

    private final DownloadInfoService downloadInfoService;

    @Autowired
    public DownloadController(DownloadInfoService downloadInfoService) {
        this.downloadInfoService = downloadInfoService;
    }

    @PostMapping("/addFile")
    public String addFile(@RequestBody DownloadRequest downloadRequest) {
        DownloadInfo downloadInfo = new DownloadInfo(downloadRequest.getUrl(), downloadRequest.getDest(), 0L, false);
        downloadInfoService.saveDownloadInfo(downloadInfo);
        return "home";
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<String> pauseDownload(@PathVariable Long id) {
        DownloadInfo downloadInfo = downloadInfoService.getDownloadInfoById(id);
        if (downloadInfo != null) {
            downloadInfo.setDownloading(false);
            downloadInfoService.saveDownloadInfo(downloadInfo);
            return ResponseEntity.ok("Download paused successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Download not found");
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<String> resumeDownload(@PathVariable Long id) {
        DownloadInfo downloadInfo = downloadInfoService.getDownloadInfoById(id);
        if (downloadInfo != null) {
            downloadInfo.setDownloading(true);
            downloadInfoService.saveDownloadInfo(downloadInfo);
            downloadFile(downloadInfo);
            return ResponseEntity.ok("Download resumed successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Download not found");
    }

    public void downloadFile(DownloadInfo downloadInfo) {
        HttpClient httpClient = HttpClient.newHttpClient();
        long resumeOffset = downloadInfo.getResumeOffset();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(downloadInfo.getFileUrl()))
                .header("Range", "bytes=" + resumeOffset + "-") // Set the Range header for resuming
                .build();

        try {
            HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

            try (InputStream inputStream = response.body();
                 FileOutputStream outputStream = new FileOutputStream(downloadInfo.getDownloadedFilePath(), true)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    resumeOffset += bytesRead; // Update resumeOffset as data is downloaded
                    downloadInfo.setResumeOffset(resumeOffset);
                    if (!downloadInfo.isDownloading()) {
                        return; // Indicates the download was paused
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<DownloadInfo>> getAllDownloads() {
        List<DownloadInfo> downloadInfos = downloadInfoService.getAllDownloadInfo();
        return ResponseEntity.ok(downloadInfos);
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<String> getDownloadProgress(@PathVariable Long id) {
        DownloadInfo downloadInfo = downloadInfoService.getDownloadInfoById(id);
        if (downloadInfo != null) {
            long totalSize = downloadInfoService.getDownloadTotalSize(downloadInfo);
            long downloaded = downloadInfo.getDownloadedFilePath().length() - downloadInfo.getResumeOffset();

            double percentage = totalSize > 0 ? ((double) downloaded / (double) totalSize) * 100.0 : 0.0;
            return ResponseEntity.ok("File " + id + " downloaded: " + percentage + "%");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Download not found");
    }

    @GetMapping("/{id}/totalSize")
    public ResponseEntity<Long> getDownloadTotalSize(@PathVariable Long id) {
        DownloadInfo downloadInfo = downloadInfoService.getDownloadInfoById(id);
        if (downloadInfo != null) {
            long totalSize = downloadInfoService.getDownloadTotalSize(downloadInfo);
            return ResponseEntity.ok(totalSize);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(-1L);
    }
}


/*
function pauseDownloads() {
    $.get("/pause", function(data) {
        console.log(data); // Log response from the server
    });
}

function resumeDownloads() {
    $.get("/resume", function(data) {
        console.log(data); // Log response from the server
    });
}
 */
