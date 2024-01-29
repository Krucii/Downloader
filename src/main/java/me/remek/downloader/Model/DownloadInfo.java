package me.remek.downloader.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Entity
@Table(name = "download_info")
@Data
public class DownloadInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users owner;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "downloaded_file_path")
    private String downloadedFilePath;

    @Column(name = "resume_offset")
    private long resumeOffset;

    @Column(name = "total_size")
    private long totalSize;

    @Column(name = "is_downloading")
    private boolean isDownloading;

    @Column(name = "download_completed")
    private boolean downloadCompleted;

    public DownloadInfo() {
        this.resumeOffset = 0;
    }

    public DownloadInfo(Users owner, String fileUrl, String downloadedFilePath, long resumeOffset, boolean isDownloading) {
        this.owner = owner;
        this.fileUrl = fileUrl;
        this.downloadedFilePath = downloadedFilePath;
        this.resumeOffset = resumeOffset;
        this.isDownloading = isDownloading;
        this.totalSize = getDownloadTotalSize(this);
    }

    private long getDownloadTotalSize(DownloadInfo downloadInfo) {
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

