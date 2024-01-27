package me.remek.downloader.Controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.remek.downloader.Model.DownloadInfo;
import me.remek.downloader.Service.DownloadInfoService;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Controller
@RequestMapping("/downloads")
public class DownloadController {

    private final DownloadInfoService downloadInfoService;
    private ExecutorService downloadExecutor = Executors.newCachedThreadPool();
    private Map<Long, Future<?>> activeDownloads = new ConcurrentHashMap<>();

    @Autowired
    public DownloadController(DownloadInfoService downloadInfoService) {
        this.downloadInfoService = downloadInfoService;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class DownloadRequest {
        private String url;
        private String dest;
    }

    @PostMapping("/addFile")
    public ResponseEntity<String> addFile(@RequestBody DownloadRequest downloadRequest) {
        DownloadInfo downloadInfo = new DownloadInfo(downloadRequest.getUrl(), downloadRequest.getDest(), 0L, false);
        downloadInfoService.saveDownloadInfo(downloadInfo);
        //startOrResumeDownload(downloadInfo);
        return ResponseEntity.ok("Download added and started successfully");
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<String> pauseDownload(@PathVariable Long id) {
        return pauseDownloadById(id);
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<String> resumeDownload(@PathVariable Long id) {
        DownloadInfo downloadInfo = downloadInfoService.getDownloadInfoById(id);
        if (downloadInfo != null && !downloadInfo.isDownloading()) {
            startOrResumeDownload(downloadInfo);
            return ResponseEntity.ok("Download resumed successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Download not found or already downloading");
    }

    private ResponseEntity<String> pauseDownloadById(Long id) {
        Future<?> task = activeDownloads.get(id);
        if (task != null) {
            task.cancel(true);
            activeDownloads.remove(id);
            DownloadInfo downloadInfo = downloadInfoService.getDownloadInfoById(id);
            if (downloadInfo != null) {
                downloadInfo.setDownloading(false);
                downloadInfoService.saveDownloadInfo(downloadInfo);
                return ResponseEntity.ok("Download paused successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Download not found");
    }

    private void startOrResumeDownload(DownloadInfo downloadInfo) {
        downloadInfo.setDownloading(true);
        downloadInfoService.saveDownloadInfo(downloadInfo);
        Runnable downloadTask = () -> downloadFile(downloadInfo);
        Future<?> future = downloadExecutor.submit(downloadTask);
        activeDownloads.put(downloadInfo.getId(), future);
    }

    private void downloadFile(DownloadInfo downloadInfo) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = buildHttpRequest(downloadInfo);

        try {
            HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            processResponse(response, downloadInfo);
        } catch (IOException | InterruptedException e) {
            if (e instanceof IOException && e.getCause() instanceof InterruptedException) {
                // Handle the InterruptedException cause
                System.out.println("Download was interrupted");
                Thread.currentThread().interrupt(); // Optionally re-interrupt the thread
            } else {
                e.printStackTrace();
            }
        } finally {
            // Ensure the download is marked as not downloading in case of an error or interruption
            downloadInfo.setDownloading(false);
            downloadInfoService.saveDownloadInfo(downloadInfo);
        }
    }

    private HttpRequest buildHttpRequest(DownloadInfo downloadInfo) {
        return HttpRequest.newBuilder()
                .uri(URI.create(downloadInfo.getFileUrl()))
                .header("Range", "bytes=" + downloadInfo.getResumeOffset() + "-")
                .build();
    }

    private void processResponse(HttpResponse<InputStream> response, DownloadInfo downloadInfo) throws IOException {
        try (InputStream inputStream = response.body();
             FileOutputStream outputStream = new FileOutputStream(downloadInfo.getDownloadedFilePath(), true)) {
            writeToOutputStream(inputStream, outputStream, downloadInfo);
        }
    }

    private void writeToOutputStream(InputStream inputStream, FileOutputStream outputStream, DownloadInfo downloadInfo) {
        byte[] buffer = new byte[8192];
        int bytesRead;
        long totalBytesRead = 0L;
        final long updateThreshold = 1024 * 1024; // Update after every 1MB downloaded

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1 && downloadInfo.isDownloading()) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                downloadInfo.setResumeOffset(downloadInfo.getResumeOffset() + bytesRead);

                if (totalBytesRead >= updateThreshold) {
                    totalBytesRead = 0L;
                    downloadInfoService.saveDownloadInfo(downloadInfo);
                }
            }
        } catch (IOException e) {
            if (e.getCause() instanceof InterruptedException) {
                System.out.println("Download was interrupted during writeToOutputStream");
                Thread.currentThread().interrupt(); // Optionally re-interrupt the thread
            } else {
                e.printStackTrace();
            }
        } finally {
            // Cleanup code to be executed regardless of interruption
            try {
                if (outputStream != null) {
                    outputStream.close(); // Close the FileOutputStream
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception as needed
            }

            try {
                if (inputStream != null) {
                    inputStream.close(); // Close the InputStream
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception as needed
            }

            // Update the download info to reflect the interrupted state
            downloadInfo.setDownloading(false);
            downloadInfoService.saveDownloadInfo(downloadInfo);
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
