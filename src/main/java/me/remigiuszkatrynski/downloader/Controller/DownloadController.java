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

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(downloadInfo.getFileUrl()))
                    .header("Range", "bytes=" + resumeOffset + "-") // Set the Range header for resuming
                    .build();

            HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

            int statusCode = response.statusCode();
            System.out.println("HTTP Status Code: " + statusCode);

            if (statusCode == 200 || statusCode == 206) {
                // Continue with reading the InputStream
                try (InputStream inputStream = response.body();
                     FileOutputStream outputStream = new FileOutputStream(downloadInfo.getDownloadedFilePath(), true)) {

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    int bytesBuffered = 0;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        bytesBuffered += bytesRead;

                        if (bytesBuffered > 1024 * 1024) {// Flush after 1MB
                            bytesBuffered = 0;
                            outputStream.flush();
                            downloadInfoService.saveDownloadInfo(downloadInfo);
                        }

                        resumeOffset += bytesRead; // Update resumeOffset as data is downloaded
                        downloadInfo.setResumeOffset(resumeOffset);
                        if (!downloadInfo.isDownloading()) {
                            return; // Indicates the download was paused
                        }
                    }
                    downloadInfoService.saveDownloadInfo(downloadInfo);
                    // Flush any remaining buffered data
                    outputStream.flush();
                }
            } else if (statusCode == 302) {
                // Handle redirection (similar to previous code)
                String newLocation = response.headers().firstValue("Location").orElse(null);
                if (newLocation != null) {
                    System.out.println("Redirecting to: " + newLocation);
                    httpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(newLocation))
                            .header("Range", "bytes=" + resumeOffset + "-")
                            .build();
                } else {
                    System.out.println("Redirect location not found in headers.");
                }
            } else {
                // Handle other status codes appropriately
                System.out.println("Unexpected HTTP Status Code: " + statusCode);
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
    public ResponseEntity<Long> getDownloadProgress(@PathVariable Long id) {
        DownloadInfo downloadInfo = downloadInfoService.getDownloadInfoById(id);
        if (downloadInfo != null) {
            return ResponseEntity.ok(downloadInfo.getResumeOffset());
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
